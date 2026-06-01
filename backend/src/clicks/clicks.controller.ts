import { Controller, Post, Body, UseGuards, Request } from '@nestjs/common';
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
}
