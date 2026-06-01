import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  ManyToOne,
  JoinColumn,
  Index,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';

@Entity('clicks')
export class Click {
  @PrimaryGeneratedColumn({ type: 'bigint' })
  id: number;

  @Column()
  user_id: number;

  @Index()
  @Column({ type: 'datetime' })
  clicked_at: Date;

  @CreateDateColumn()
  created_at: Date;

  @ManyToOne(() => User, (user) => user.clicks)
  @JoinColumn({ name: 'user_id' })
  user: User;
}
