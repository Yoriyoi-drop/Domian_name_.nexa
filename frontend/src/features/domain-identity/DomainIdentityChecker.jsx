import React, { useState, useEffect } from 'react';
import { Search, Globe, Zap, Shield, TrendingUp, CheckCircle } from 'lucide-react';
import { useApi } from '../../core/hooks/useApi';

const DomainIdentityChecker = () => {
  const [domainName, setDomainName] = useState('');
  const [searchResults, setSearchResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [identityInfo, setIdentityInfo] = useState(null);
  const { get } = useApi();

  const handleSearch = async () => {
    if (!domainName.trim()) return;
    
    setLoading(true);
    try {
      // Check domain availability
      const availabilityResponse = await get(`/api/v1/domain/availability/${domainName}`);
      setSearchResults(availabilityResponse.data);
      
      // Get domain identity info
      const identityResponse = await get(`/api/v1/domain/identity/${domainName}`);
      setIdentityInfo(identityResponse.data);
    } catch (error) {
      console.error('Error checking domain:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 p-4 md:p-8">
      <div className="max-w-6xl mx-auto">
        {/* Hero Section */}
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Discover the Power of <span className="text-blue-600">.nexa</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Find your perfect domain with strategic advantages that other TLDs can't offer. 
            Premium names still available, sovereign identity, and universal scope.
          </p>
        </div>

        {/* Search Section */}
        <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <Globe className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                value={domainName}
                onChange={(e) => setDomainName(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Enter your desired domain name (e.g., yourbrand.nexa)"
                className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <button
              onClick={handleSearch}
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center justify-center gap-2"
            >
              {loading ? (
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
              ) : (
                <>
                  <Search className="w-5 h-5" />
                  Check Availability
                </>
              )}
            </button>
          </div>
        </div>

        {/* Results Section */}
        {searchResults && (
          <div className="grid md:grid-cols-2 gap-8 mb-8">
            {/* Availability Results */}
            <div className="bg-white rounded-2xl shadow-xl p-6">
              <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center gap-2">
                <Globe className="w-6 h-6 text-blue-600" />
                Domain Availability
              </h2>
              
              <div className={`p-4 rounded-lg mb-4 ${searchResults.available ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'}`}>
                <div className="flex items-center justify-between">
                  <span className="text-lg font-semibold">{searchResults.domain}</span>
                  {searchResults.available ? (
                    <div className="flex items-center text-green-600">
                      <CheckCircle className="w-5 h-5 mr-2" />
                      Available
                    </div>
                  ) : (
                    <div className="text-red-600">Not Available</div>
                  )}
                </div>
                
                {!searchResults.available && searchResults.reason && (
                  <p className="text-red-600 mt-2">{searchResults.reason}</p>
                )}
                
                {searchResults.premiumRanking && (
                  <div className="mt-3">
                    <div className="flex justify-between text-sm mb-1">
                      <span>Premium Ranking</span>
                      <span>{searchResults.premiumRanking}/100</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div 
                        className={`h-2 rounded-full ${
                          searchResults.premiumRanking > 70 ? 'bg-green-500' :
                          searchResults.premiumRanking > 40 ? 'bg-yellow-500' : 'bg-red-500'
                        }`}
                        style={{ width: `${searchResults.premiumRanking}%` }}
                      ></div>
                    </div>
                  </div>
                )}
              </div>

              {searchResults.suggestedAlternatives && searchResults.suggestedAlternatives.length > 0 && (
                <div>
                  <h3 className="font-semibold text-gray-700 mb-2">Suggested Alternatives:</h3>
                  <ul className="space-y-1">
                    {searchResults.suggestedAlternatives.map((alt, index) => (
                      <li key={index} className="text-blue-600 hover:text-blue-800 cursor-pointer">
                        {alt}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>

            {/* Identity Benefits */}
            {identityInfo && (
              <div className="bg-white rounded-2xl shadow-xl p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center gap-2">
                  <Zap className="w-6 h-6 text-yellow-600" />
                  Strategic Advantages
                </h2>
                
                <div className="space-y-3">
                  {identityInfo.identityBenefits?.map((benefit, index) => (
                    <div key={index} className="flex items-start gap-3 p-3 bg-blue-50 rounded-lg">
                      <CheckCircle className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
                      <span className="text-gray-700">{benefit}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Comparison Section */}
        <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">
            Why <span className="text-blue-600">.nexa</span> Outperforms Other TLDs
          </h2>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div className="border border-blue-200 rounded-xl p-5 bg-gradient-to-br from-blue-50 to-indigo-50">
              <Shield className="w-10 h-10 text-blue-600 mb-3" />
              <h3 className="font-bold text-lg mb-2">Sovereign Identity</h3>
              <p className="text-gray-600 text-sm">
                Unlike .io and .ai ccTLDs with geopolitical risks, .nexa offers stable, 
                neutral domain identity without territorial concerns.
              </p>
            </div>
            
            <div className="border border-green-200 rounded-xl p-5 bg-gradient-to-br from-green-50 to-emerald-50">
              <TrendingUp className="w-10 h-10 text-green-600 mb-3" />
              <h3 className="font-bold text-lg mb-2">Premium Availability</h3>
              <p className="text-gray-600 text-sm">
                While .com names are saturated, short, memorable .nexa domains are still available 
                for your brand identity.
              </p>
            </div>
            
            <div className="border border-purple-200 rounded-xl p-5 bg-gradient-to-br from-purple-50 to-violet-50">
              <Zap className="w-10 h-10 text-purple-600 mb-3" />
              <h3 className="font-bold text-lg mb-2">Universal Scope</h3>
              <p className="text-gray-600 text-sm">
                Unlike niche TLDs like .app or .dev, .nexa works perfectly for any business aspect 
                - marketing, product, or development.
              </p>
            </div>
          </div>
        </div>

        {/* TLD Comparison Table */}
        <div className="bg-white rounded-2xl shadow-xl p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">TLD Comparison</h2>
          
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-4">TLD</th>
                  <th className="text-left py-3 px-4">Availability</th>
                  <th className="text-left py-3 px-4">Geopolitical Risk</th>
                  <th className="text-left py-3 px-4">Scope</th>
                  <th className="text-left py-3 px-4">Modern Perception</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b">
                  <td className="py-3 px-4 font-semibold">.com</td>
                  <td className="py-3 px-4 text-red-600">❌ Saturated</td>
                  <td className="py-3 px-4 text-green-600">✅ None</td>
                  <td className="py-3 px-4 text-green-600">✅ Universal</td>
                  <td className="py-3 px-4 text-red-600">❌ Legacy</td>
                </tr>
                <tr className="border-b">
                  <td className="py-3 px-4 font-semibold">.io</td>
                  <td className="py-3 px-4 text-yellow-600">✅ Available</td>
                  <td className="py-3 px-4 text-red-600">❌ High</td>
                  <td className="py-3 px-4 text-green-600">✅ Universal</td>
                  <td className="py-3 px-4 text-green-600">✅ Modern</td>
                </tr>
                <tr className="border-b">
                  <td className="py-3 px-4 font-semibold">.app</td>
                  <td className="py-3 px-4 text-yellow-600">✅ Available</td>
                  <td className="py-3 px-4 text-green-600">✅ None</td>
                  <td className="py-3 px-4 text-red-600">❌ Niche</td>
                  <td className="py-3 px-4 text-green-600">✅ Modern</td>
                </tr>
                <tr className="border-b bg-blue-50">
                  <td className="py-3 px-4 font-semibold text-blue-700">.nexa</td>
                  <td className="py-3 px-4 text-green-600">✅ Available</td>
                  <td className="py-3 px-4 text-green-600">✅ None</td>
                  <td className="py-3 px-4 text-green-600">✅ Universal</td>
                  <td className="py-3 px-4 text-green-600">✅ Modern</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DomainIdentityChecker;