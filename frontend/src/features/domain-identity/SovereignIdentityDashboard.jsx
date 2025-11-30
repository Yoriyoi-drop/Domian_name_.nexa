import React, { useState, useEffect } from 'react';
import { Shield, Globe, AlertTriangle, CheckCircle, BarChart3, Server, Star } from 'lucide-react';
import { useApi } from '../../core/hooks/useApi';

const SovereignIdentityDashboard = () => {
  const [domainName, setDomainName] = useState('myproject.nexa');
  const [identityInfo, setIdentityInfo] = useState(null);
  const [trustIndicators, setTrustIndicators] = useState(null);
  const [geopoliticalRisk, setGeopoliticalRisk] = useState({});
  const [riskRecommendations, setRiskRecommendations] = useState([]);
  const [isDomainSafe, setIsDomainSafe] = useState(null);
  const { get } = useApi();

  const fetchSovereignIdentity = async () => {
    try {
      // Get sovereign identity info
      const identityResponse = await get(`/api/v1/sovereign/identity/${domainName}`);
      setIdentityInfo(identityResponse.data);

      // Get trust indicators
      const trustResponse = await get(`/api/v1/sovereign/trust/${domainName}`);
      setTrustIndicators(trustResponse.data);

      // Get geopolitical risk report
      const riskResponse = await get('/api/v1/sovereign/geopolitical-risk-report');
      setGeopoliticalRisk(riskResponse.data);

      // Check if domain is geopolitically safe
      const safeResponse = await get(`/api/v1/sovereign/is-safe/${domainName}`);
      setIsDomainSafe(safeResponse.data);

      // Get risk mitigation recommendations
      const recommendationsResponse = await get(`/api/v1/sovereign/risk-mitigation/${domainName}`);
      setRiskRecommendations(recommendationsResponse.data);
    } catch (error) {
      console.error('Error fetching sovereign identity data:', error);
    }
  };

  useEffect(() => {
    if (domainName) {
      fetchSovereignIdentity();
    }
  }, [domainName]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 p-4 md:p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="flex items-center justify-center gap-3 mb-4">
            <Shield className="w-10 h-10 text-blue-600" />
            <h1 className="text-3xl md:text-4xl font-bold text-gray-900">
              Sovereign Identity Dashboard
            </h1>
          </div>
          <p className="text-lg text-gray-600 max-w-3xl mx-auto">
            Verify the geopolitical neutrality and trustworthiness of your .nexa domain. 
            Unlike other TLDs, .nexa offers complete sovereignty and zero territorial risk.
          </p>
        </div>

        {/* Domain Input */}
        <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
          <div className="flex flex-col md:flex-row gap-4 items-center">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Domain Name
              </label>
              <input
                type="text"
                value={domainName}
                onChange={(e) => setDomainName(e.target.value)}
                placeholder="Enter domain name (e.g., myproject.nexa)"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <button
              onClick={fetchSovereignIdentity}
              className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center gap-2 mt-6 md:mt-0"
            >
              <Globe className="w-5 h-5" />
              Verify Domain
            </button>
          </div>
        </div>

        {identityInfo && (
          <div className="grid lg:grid-cols-2 gap-8 mb-8">
            {/* Sovereign Status */}
            <div className="bg-white rounded-2xl shadow-xl p-6">
              <div className="flex items-center gap-3 mb-4">
                <Shield className="w-8 h-8 text-green-600" />
                <h2 className="text-2xl font-bold text-gray-900">Sovereign Status</h2>
              </div>
              
              <div className="space-y-4">
                <div className="flex justify-between items-center p-4 bg-green-50 rounded-lg">
                  <span className="font-semibold">Sovereign Status</span>
                  <span className="text-green-600 font-bold">{identityInfo.sovereignStatus}</span>
                </div>
                
                <div className="flex justify-between items-center p-4 bg-blue-50 rounded-lg">
                  <span className="font-semibold">Geopolitical Risk</span>
                  <span className="text-green-600 font-bold">{identityInfo.geopoliticalRiskLevel}</span>
                </div>
                
                <div className="flex justify-between items-center p-4 bg-purple-50 rounded-lg">
                  <span className="font-semibold">Jurisdiction</span>
                  <span className="text-gray-700">{identityInfo.jurisdiction}</span>
                </div>
                
                <div className="flex justify-between items-center p-4 bg-indigo-50 rounded-lg">
                  <span className="font-semibold">Governance Model</span>
                  <span className="text-gray-700">{identityInfo.governanceModel}</span>
                </div>
              </div>
            </div>

            {/* Trust Indicators */}
            <div className="bg-white rounded-2xl shadow-xl p-6">
              <div className="flex items-center gap-3 mb-4">
                <Star className="w-8 h-8 text-yellow-600" />
                <h2 className="text-2xl font-bold text-gray-900">Trust Indicators</h2>
              </div>
              
              <div className="space-y-4">
                <div className="flex justify-between items-center p-4 bg-green-50 rounded-lg">
                  <span className="font-semibold">Uptime</span>
                  <span className="text-green-600 font-bold">{identityInfo.availabilityMetrics?.uptime_30d}%</span>
                </div>
                
                <div className="flex justify-between items-center p-4 bg-blue-50 rounded-lg">
                  <span className="font-semibold">Security Score</span>
                  <span className="text-green-600 font-bold">{identityInfo.securityScore}/100</span>
                </div>
                
                <div className="flex justify-between items-center p-4 bg-purple-50 rounded-lg">
                  <span className="font-semibold">Compliance</span>
                  <span className="text-gray-700">{identityInfo.complianceStatus}</span>
                </div>
                
                <div className="p-4 bg-indigo-50 rounded-lg">
                  <div className="font-semibold mb-2">Security Features</div>
                  <div className="text-sm text-gray-600">
                    {Array.isArray(identityInfo.trustIndicators?.security_features) 
                      ? identityInfo.trustIndicators.security_features.join(', ') 
                      : 'DNSSEC, DANE, RPKI'}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Risk Comparison */}
        <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
          <div className="flex items-center gap-3 mb-6">
            <AlertTriangle className="w-8 h-8 text-orange-600" />
            <h2 className="text-2xl font-bold text-gray-900">Geopolitical Risk Comparison</h2>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-4">TLD</th>
                  <th className="text-left py-3 px-4">Geopolitical Risk</th>
                  <th className="text-left py-3 px-4">Jurisdiction</th>
                  <th className="text-left py-3 px-4">Safety</th>
                </tr>
              </thead>
              <tbody>
                {Object.entries(geopoliticalRisk).map(([tld, risk]) => (
                  <tr key={tld} className="border-b">
                    <td className="py-3 px-4 font-semibold">{tld}</td>
                    <td className="py-3 px-4">{risk}</td>
                    <td className="py-3 px-4">
                      {tld === '.nexa' ? 'Neutral International Registry' : 
                       tld === '.io' ? 'British Indian Ocean Territory' :
                       tld === '.ai' ? 'Anguilla' :
                       'Country-specific'}
                    </td>
                    <td className="py-3 px-4">
                      {tld === '.nexa' ? (
                        <div className="flex items-center text-green-600">
                          <CheckCircle className="w-5 h-5 mr-1" />
                          Safe
                        </div>
                      ) : (
                        <div className="flex items-center text-red-600">
                          <AlertTriangle className="w-5 h-5 mr-1" />
                          Risk
                        </div>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Risk Recommendations */}
        {riskRecommendations && riskRecommendations.length > 0 && (
          <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
            <div className="flex items-center gap-3 mb-6">
              <BarChart3 className="w-8 h-8 text-blue-600" />
              <h2 className="text-2xl font-bold text-gray-900">Risk Mitigation</h2>
            </div>
            
            <div className="space-y-3">
              {riskRecommendations.map((rec, index) => (
                <div key={index} className="flex items-start gap-3 p-4 bg-blue-50 rounded-lg">
                  <CheckCircle className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
                  <span className="text-gray-700">{rec}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Domain Safety Status */}
        {isDomainSafe !== null && (
          <div className={`bg-white rounded-2xl shadow-xl p-6 ${
            isDomainSafe ? 'border-l-4 border-green-500' : 'border-l-4 border-red-500'
          }`}>
            <div className="flex items-center gap-3 mb-4">
              {isDomainSafe ? (
                <CheckCircle className="w-8 h-8 text-green-600" />
              ) : (
                <AlertTriangle className="w-8 h-8 text-red-600" />
              )}
              <h2 className="text-2xl font-bold text-gray-900">
                Domain Safety Status
              </h2>
            </div>
            
            <div className="text-lg">
              {isDomainSafe ? (
                <div className="text-green-600">
                  <Shield className="w-6 h-6 inline mr-2" />
                  Your domain ({domainName}) is geopolitically sovereign and secure from territorial risks.
                </div>
              ) : (
                <div className="text-red-600">
                  <AlertTriangle className="w-6 h-6 inline mr-2" />
                  Your domain ({domainName}) may be subject to geopolitical risks. Consider migrating to .nexa.
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SovereignIdentityDashboard;