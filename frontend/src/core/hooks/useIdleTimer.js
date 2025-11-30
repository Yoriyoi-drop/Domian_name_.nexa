import { useState, useEffect, useCallback } from 'react';

const useIdleTimer = (onIdle, timeout = 15 * 60 * 1000, onActive) => { // Default 15 minutes
  const [remainingTime, setRemainingTime] = useState(timeout);
  const [isIdle, setIsIdle] = useState(false);

  // Reset the timer
  const resetTimer = useCallback(() => {
    setIsIdle(false);
    setRemainingTime(timeout);
  }, [timeout]);

  // Handle user activity
  const handleActivity = useCallback(() => {
    resetTimer();
    if (isIdle && onActive) {
      onActive();
    }
  }, [resetTimer, isIdle, onActive]);

  useEffect(() => {
    // Set up event listeners for user activity
    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click', 'focus'];

    events.forEach(event => {
      document.addEventListener(event, handleActivity, true);
    });

    // Timer interval
    let interval;
    if (!isIdle) {
      interval = setInterval(() => {
        setRemainingTime(prev => {
          if (prev <= 1000) { // Less than 1 second remaining
            setIsIdle(true);
            if (onIdle) onIdle();
            clearInterval(interval);
            return 0;
          }
          return prev - 1000;
        });
      }, 1000);
    }

    return () => {
      // Clean up event listeners and interval
      events.forEach(event => {
        document.removeEventListener(event, handleActivity, true);
      });
      if (interval) clearInterval(interval);
    };
  }, [handleActivity, isIdle, onIdle]);

  // Format time for display (mm:ss)
  const formatTime = (timeInMs) => {
    const totalSeconds = Math.floor(timeInMs / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  };

  return {
    isIdle,
    remainingTime,
    timeFormatted: formatTime(remainingTime),
    resetTimer,
    setIsIdle
  };
};

export default useIdleTimer;