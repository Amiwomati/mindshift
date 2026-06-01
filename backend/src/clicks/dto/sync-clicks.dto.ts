import { IsArray, ArrayNotEmpty, ValidateNested, IsDateString } from 'class-validator';
import { Type } from 'class-transformer';

export class ClickItemDto {
  @IsDateString()
  clicked_at: string;
}

export class SyncClicksDto {
  @IsArray()
  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => ClickItemDto)
  clicks: ClickItemDto[];
}
