import { Controller, Post, Get, Body, Query, UseGuards, Request } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { ClicksService } from './clicks.service';
import { SyncClicksDto } from './dto/sync-clicks.dto';

@Controller('clicks')
export class ClicksController {
  constructor(private readonly clicksService: ClicksService) {}

  @UseGuards(JwtAuthGuard)
  @Post('sync')
  sync(@Request() req, @Body() dto: SyncClicksDto) {
    return this.clicksService.syncClicks(req.user.id, dto);
  }

  @UseGuards(JwtAuthGuard)
  @Get()
  getClicks(
    @Request() req,
    @Query('page') page = '1',
    @Query('limit') limit = '20',
  ) {
    return this.clicksService.getClicks(req.user.id, Number(page), Number(limit));
  }

  @UseGuards(JwtAuthGuard)
  @Get('stats')
  getStats(@Request() req) {
    return this.clicksService.getStats(req.user.id);
  }
}
