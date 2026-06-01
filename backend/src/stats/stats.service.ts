import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Click } from '../clicks/entities/click.entity';

@Injectable()
export class StatsService {
  constructor(
    @InjectRepository(Click)
    private readonly clickRepository: Repository<Click>,
  ) {}

  private getDateRange(from?: string, to?: string) {
    const today = new Date().toISOString().split('T')[0];
    return {
      fromDate: from || today,
      toDate: to || today,
    };
  }

  async getTopPatients(from?: string, to?: string) {
    const { fromDate, toDate } = this.getDateRange(from, to);

    const results = await this.clickRepository
      .createQueryBuilder('c')
      .select('c.user_id', 'user_id')
      .addSelect('u.name', 'name')
      .addSelect('u.email', 'email')
      .addSelect('COUNT(c.id)', 'count')
      .innerJoin('c.user', 'u')
      .where('DATE(c.clicked_at) BETWEEN :from AND :to', {
        from: fromDate,
        to: toDate,
      })
      .groupBy('c.user_id')
      .orderBy('count', 'DESC')
      .limit(10)
      .getRawMany();

    return {
      data: results.map((r) => ({
        user_id: r.user_id,
        name: r.name,
        email: r.email,
        count: Number(r.count),
      })),
      from: fromDate,
      to: toDate,
    };
  }

  async getTotal(from?: string, to?: string) {
    const { fromDate, toDate } = this.getDateRange(from, to);

    const result = await this.clickRepository
      .createQueryBuilder('c')
      .select('COUNT(c.id)', 'total')
      .where('DATE(c.clicked_at) BETWEEN :from AND :to', {
        from: fromDate,
        to: toDate,
      })
      .getRawOne();

    return {
      total: Number(result.total),
      from: fromDate,
      to: toDate,
    };
  }
}
