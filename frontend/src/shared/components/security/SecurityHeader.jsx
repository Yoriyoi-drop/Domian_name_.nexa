import React from 'react';
import { Shield, Lock, Globe, CheckCircle } from 'lucide-react';

const SecurityHeader = ({ showTrustBadge = true }) => {
  return (
    <div className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white p-4">
      <div className="max-w-7xl mx-auto">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-3">
            <Shield className="w-8 h-8 text-yellow-300" />
            <div>
              <h2 className="text-xl font-bold">Secure .nexa Domain</h2>
              <p className="text-blue-100 text-sm">Geopolitically sovereign • Enterprise secure</p>
            </div>
          </div>
          
          {showTrustBadge && (
            <div className="flex flex-wrap gap-4 justify-center">
              <div className="flex items-center gap-2 bg-white/20 backdrop-blur-sm px-3 py-2 rounded-lg">
                <CheckCircle className="w-5 h-5 text-green-300" />
                <span className="text-sm">SOC 2 Compliant</span>
              </div>
              <div className="flex items-center gap-2 bg-white/20 backdrop-blur-sm px-3 py-2 rounded-lg">
                <CheckCircle className="w-5 h-5 text-green-300" />
                <span className="text-sm">ISO 27001</span>
              </div>
              <div className="flex items-center gap-2 bg-white/20 backdrop-blur-sm px-3 py-2 rounded-lg">
                <Lock className="w-5 h-5 text-green-300" />
                <span className="text-sm">DNSSEC Protected</span>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SecurityHeader;