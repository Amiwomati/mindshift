import { Process, Processor } from '@nestjs/bull';
import { Logger } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Job } from 'bull';
import { Click } from './entities/click.entity';

interface SyncJobData {
  userId: number;
  clicks: Array<{ clicked_at: string }>;
}

@Processor('clicks-sync')
export class ClicksProcessor {
  private readonly logger = new Logger(ClicksProcessor.name);

  constructor(
    @InjectRepository(Click)
    private readonly clickRepository: Repository<Click>,
  ) {}

  @Process('sync')
  async processSyncJob(job: Job<SyncJobData>) {
    const { userId, clicks } = job.data;
    const CHUNK_SIZE = 100;
    let saved = 0;

    for (let i = 0; i < clicks.length; i += CHUNK_SIZE) {
      const chunk = clicks.slice(i, i + CHUNK_SIZE);
      const values = chunk.map((c) => ({
        user_id: userId,
        clicked_at: new Date(c.clicked_at),
      }));
      await this.clickRepository
        .createQueryBuilder()
        .insert()
        .into(Click)
        .values(values)
        .execute();
      saved += chunk.length;
    }

    this.logger.log(`Saved ${saved} clicks for user ${userId}`);
  }
}
