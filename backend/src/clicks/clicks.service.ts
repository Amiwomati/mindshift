import { Injectable } from '@nestjs/common';
import { InjectQueue } from '@nestjs/bull';
import { InjectRepository } from '@nestjs/typeorm';
import { Queue } from 'bull';
import { Repository } from 'typeorm';
import { Click } from './entities/click.entity';
import { SyncClicksDto } from './dto/sync-clicks.dto';

@Injectable()
export class ClicksService {
  constructor(
    @InjectQueue('clicks-sync') private readonly clicksQueue: Queue,
    @InjectRepository(Click) private readonly clickRepository: Repository<Click>,
  ) {}

  async syncClicks(userId: number, dto: SyncClicksDto) {
    await this.clicksQueue.add('sync', {
      userId,
      clicks: dto.clicks,
    });

    return {
      message: `${dto.clicks.length} clicks encolados para procesamiento`,
      queued: dto.clicks.length,
    };
  }

  async getClicks(userId: number, page: number, limit: number) {
    const [items, total] = await this.clickRepository.findAndCount({
      where: { user_id: userId },
      order: { clicked_at: 'DESC' },
      skip: (page - 1) * limit,
      take: limit,
    });

    return {
      data: items.map((c) => ({ id: c.id, clicked_at: c.clicked_at })),
      total,
      page,
      last_page: Math.ceil(total / limit),
    };
  }

  async getStats(userId: number) {
    const rows = await this.clickRepository
      .createQueryBuilder('c')
      .select("DATE_FORMAT(c.clicked_at, '%Y-%m-%d')", 'date')
      .addSelect('COUNT(*)', 'count')
      .where('c.user_id = :userId', { userId })
      .groupBy('date')
      .orderBy('date', 'DESC')
      .getRawMany();

    return rows.map((r) => ({ date: r.date, count: Number(r.count) }));
  }
}
