import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Click } from '../clicks/entities/click.entity';
import { StatsController } from './stats.controller';
import { StatsService } from './stats.service';

@Module({
  imports: [TypeOrmModule.forFeature([Click])],
  controllers: [StatsController],
  providers: [StatsService],
})
export class StatsModule {}
