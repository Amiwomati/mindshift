import { Controller, Get, Query } from '@nestjs/common';
import { StatsService } from './stats.service';

@Controller('stats')
export class StatsController {
  constructor(private readonly statsService: StatsService) {}

  @Get('top-patients')
  getTopPatients(
    @Query('from') from?: string,
    @Query('to') to?: string,
  ) {
    return this.statsService.getTopPatients(from, to);
  }

  @Get('total')
  getTotal(
    @Query('from') from?: string,
    @Query('to') to?: string,
  ) {
    return this.statsService.getTotal(from, to);
  }
}
