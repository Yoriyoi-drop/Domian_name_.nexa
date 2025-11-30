import React from 'react';
import { BadgeDollarSign, Shield, Zap, Crown } from 'lucide-react';

const NexaIdentityBadge = ({ 
  type = 'standard', 
  size = 'medium',
  showLabel = true,
  className = '' 
}) => {
  const getBadgeConfig = () => {
    switch (type) {
      case 'premium':
        return {
          icon: Crown,
          label: 'Premium .nexa Domain',
          bgClass: 'bg-gradient-to-r from-yellow-400 to-orange-500',
          textClass: 'text-white',
          iconClass: 'text-white'
        };
      case 'secure':
        return {
          icon: Shield,
          label: 'Sovereign Identity',
          bgClass: 'bg-gradient-to-r from-blue-500 to-indigo-600',
          textClass: 'text-white',
          iconClass: 'text-white'
        };
      case 'innovation':
        return {
          icon: Zap,
          label: 'Future-First Innovation',
          bgClass: 'bg-gradient-to-r from-purple-500 to-pink-500',
          textClass: 'text-white',
          iconClass: 'text-white'
        };
      case 'standard':
      default:
        return {
          icon: BadgeDollarSign,
          label: 'Powered by .nexa',
          bgClass: 'bg-gradient-to-r from-blue-600 to-blue-800',
          textClass: 'text-white',
          iconClass: 'text-white'
        };
    }
  };

  const config = getBadgeConfig();
  const IconComponent = config.icon;
  
  const sizeClasses = {
    small: 'px-2 py-1 text-xs',
    medium: 'px-3 py-1.5 text-sm',
    large: 'px-4 py-2 text-base'
  };

  return (
    <div className={`inline-flex items-center ${config.bgClass} ${sizeClasses[size]} rounded-full ${className}`}>
      <IconComponent className={`w-4 h-4 mr-1 ${config.iconClass}`} />
      {showLabel && (
        <span className={`font-medium ${config.textClass}`}>
          {config.label}
        </span>
      )}
    </div>
  );
};

export default NexaIdentityBadge;