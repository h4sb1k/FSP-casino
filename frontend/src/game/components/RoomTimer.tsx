/**
 * Таймер обратного отсчета для раунда
 */
import React, { useState, useEffect } from 'react';
import './RoomTimer.css';

interface RoomTimerProps {
  startTime: string;
  endTime?: string;
  durationSeconds?: number;
}

const RoomTimer: React.FC<RoomTimerProps> = ({ 
  startTime, 
  endTime,
  durationSeconds = 60 
}) => {
  const [timeLeft, setTimeLeft] = useState<number>(durationSeconds);
  const [isFinished, setIsFinished] = useState(false);

  useEffect(() => {
    const start = new Date(startTime).getTime();
    const end = endTime ? new Date(endTime).getTime() : start + (durationSeconds * 1000);
    
    const calculateTimeLeft = () => {
      const now = Date.now();
      const difference = end - now;
      
      if (difference <= 0) {
        setTimeLeft(0);
        setIsFinished(true);
        return 0;
      }
      
      return Math.floor(difference / 1000);
    };

    setTimeLeft(calculateTimeLeft());

    const timer = setInterval(() => {
      const left = calculateTimeLeft();
      setTimeLeft(left);
      
      if (left <= 0) {
        clearInterval(timer);
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [startTime, endTime, durationSeconds]);

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const getTimerClass = (): string => {
    if (isFinished) return 'timer-finished';
    if (timeLeft <= 10) return 'timer-critical';
    if (timeLeft <= 30) return 'timer-warning';
    return 'timer-normal';
  };

  return (
    <div className={`room-timer ${getTimerClass()}`}>
      <div className="timer-label">
        {isFinished ? 'Раунд завершен' : 'До начала раунда:'}
      </div>
      <div className="timer-value">
        {formatTime(timeLeft)}
      </div>
      {!isFinished && timeLeft <= 10 && (
        <div className="timer-pulse"></div>
      )}
    </div>
  );
};

export default RoomTimer;
