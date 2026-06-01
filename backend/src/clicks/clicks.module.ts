import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { BullModule } from '@nestjs/bull';
import { Click } from './entities/click.entity';
import { ClicksController } from './clicks.controller';
import { ClicksService } from './clicks.service';
import { ClicksProcessor } from './clicks.processor';

@Module({
  imports: [
    TypeOrmModule.forFeature([Click]),
    BullModule.registerQueue({ name: 'clicks-sync' }),
  ],
  controllers: [ClicksController],
  providers: [ClicksService, ClicksProcessor],
})
export class ClicksModule {}
