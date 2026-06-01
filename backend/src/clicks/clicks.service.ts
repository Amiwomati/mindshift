import { Injectable } from '@nestjs/common';
import { InjectQueue } from '@nestjs/bull';
import { Queue } from 'bull';
import { SyncClicksDto } from './dto/sync-clicks.dto';

@Injectable()
export class ClicksService {
  constructor(
    @InjectQueue('clicks-sync') private readonly clicksQueue: Queue,
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
}
