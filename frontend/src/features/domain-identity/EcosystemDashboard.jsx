import React, { useState, useEffect } from 'react';
import { Globe, TrendingUp, Users, ShoppingCart, BarChart3, Share2, Star, Target, Award } from 'lucide-react';
import { useApi } from '../../core/hooks/useApi';

const EcosystemDashboard = () => {
  const [ecosystemData, setEcosystemData] = useState(null);
  const [referralData, setReferralData] = useState(null);
  const [marketplaceData, setMarketplaceData] = useState(null);
  const [marketingData, setMarketingData] = useState(null);
  const [domainName, setDomainName] = useState('myproject.nexa');
  const [activeTab, setActiveTab] = useState('overview');
  const [referralCode, setReferralCode] = useState('');
  const [newUserId, setNewUserId] = useState('');
  const [processSuccess, setProcessSuccess] = useState(false);
  const { get, post } = useApi();

  const fetchEcosystemData = async () => {
    try {
      const response = await get(`/api/v1/ecosystem/analytics/${domainName}`);
      setEcosystemData(response.data);
      
      const marketResponse = await get('/api/v1/ecosystem/marketplace?page=0&size=5');
      setMarketplaceData(marketResponse.data);
      
      const marketingResponse = await get(`/api/v1/ecosystem/marketing-insights/${domainName}`);
      setMarketingData(marketingResponse.data);
    } catch (error) {
      console.error('Error fetching ecosystem data:', error);
    }
  };

  const fetchReferralData = async () => {
    try {
      // We'll use a mock user ID for demo purposes
      const response = await get(`/api/v1/ecosystem/referral-program/user123`);
      setReferralData(response.data);
    } catch (error) {
      console.error('Error fetching referral data:', error);
    }
  };

  const createReferralCode = async () => {
    try {
      const response = await post('/api/v1/ecosystem/referral-program/user123/create-code');
      setReferralCode(response.data);
      alert(`New referral code created: ${response.data}`);
    } catch (error) {
      console.error('Error creating referral code:', error);
    }
  };

  const processReferral = async () => {
    try {
      const response = await post(`/api/v1/ecosystem/referral-program/process/${referralCode}`, null, {
        params: { newUserId }
      });
      setProcessSuccess(response.data);
      alert(response.data ? 'Referral processed successfully!' : 'Referral code not valid');
    } catch (error) {
      console.error('Error processing referral:', error);
    }
  };

  useEffect(() => {
    fetchEcosystemData();
    fetchReferralData();
  }, [domainName]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-purple-50 p-4 md:p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="flex items-center justify-center gap-3 mb-4">
            <Globe className="w-10 h-10 text-purple-600" />
            <h1 className="text-3xl md:text-4xl font-bold text-gray-900">
              Ecosystem & Marketing Dashboard
            </h1>
          </div>
          <p className="text-lg text-gray-600 max-w-3xl mx-auto">
            Exclusive ecosystem features that make .nexa the premier choice for innovative enterprises. 
            Unique value propositions competitors cannot replicate.
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
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              />
            </div>
            <button
              onClick={fetchEcosystemData}
              className="bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-medium transition-colors"
            >
              Analyze Ecosystem
            </button>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-2 mb-8 overflow-x-auto">
          <button
            onClick={() => setActiveTab('overview')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap ${
              activeTab === 'overview'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Overview
          </button>
          <button
            onClick={() => setActiveTab('referral')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap ${
              activeTab === 'referral'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Referral Program
          </button>
          <button
            onClick={() => setActiveTab('marketplace')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap ${
              activeTab === 'marketplace'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Premium Marketplace
          </button>
          <button
            onClick={() => setActiveTab('marketing')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap ${
              activeTab === 'marketing'
                ? 'bg-purple-600 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Marketing Insights
          </button>
        </div>

        {activeTab === 'overview' && ecosystemData && (
          <div className="grid lg:grid-cols-2 gap-8 mb-8">
            {/* Ecosystem Analytics */}
            <div className="bg-white rounded-2xl shadow-xl p-6">
              <div className="flex items-center gap-3 mb-4">
                <Globe className="w-8 h-8 text-purple-600" />
                <h2 className="text-2xl font-bold text-gray-900">Ecosystem Analytics</h2>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div className="p-4 bg-green-50 rounded-lg">
                  <div className="text-2xl font-bold text-green-600">{ecosystemData.totalReferrals}</div>
                  <div className="text-gray-600">Total Referrals</div>
                </div>
                
                <div className="p-4 bg-blue-50 rounded-lg">
                  <div className="text-2xl font-bold text-blue-600">{ecosystemData.integrationCount}</div>
                  <div className="text-gray-600">Integrations</div>
                </div>
                
                <div className="p-4 bg-yellow-50 rounded-lg">
                  <div className="text-2xl font-bold text-yellow-600">
                    {ecosystemData.communityEngagement?.monthlyActiveUsers?.toLocaleString() || '45,000'}
                  </div>
                  <div className="text-gray-600">Active Users</div>
                </div>
                
                <div className="p-4 bg-indigo-50 rounded-lg">
                  <div className="text-2xl font-bold text-indigo-600">
                    {ecosystemData.ecosystemGrowthMetrics?.monthlyGrowthRate || '15.2'}%
                  </div>
                  <div className="text-gray-600">Growth Rate</div>
                </div>
              </div>
              
              <div className="mt-4">
                <h3 className="font-semibold text-gray-900 mb-2">Premium Feature Adoption</h3>
                <div className="space-y-2">
                  {ecosystemData.premiumFeatureAdoption && Object.entries(ecosystemData.premiumFeatureAdoption).map(([feature, rate]) => (
                    <div key={feature} className="flex justify-between items-center">
                      <span className="text-sm text-gray-700 capitalize">{feature.replace(/([A-Z])/g, ' $1')}</span>
                      <div className="flex items-center gap-2">
                        <div className="w-24 bg-gray-200 rounded-full h-2">
                          <div 
                            className="bg-purple-500 h-2 rounded-full" 
                            style={{ width: `${rate}%` }}
                          ></div>
                        </div>
                        <span className="text-sm font-semibold">{rate}%</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Partnership Opportunities */}
            <div className="bg-white rounded-2xl shadow-xl p-6">
              <div className="flex items-center gap-3 mb-4">
                <Target className="w-8 h-8 text-green-600" />
                <h2 className="text-2xl font-bold text-gray-900">Partnership Opportunities</h2>
              </div>
              
              <div className="space-y-3">
                {ecosystemData.partnershipOpportunities?.map((opportunity, index) => (
                  <div key={index} className="flex items-start gap-3 p-3 bg-blue-50 rounded-lg">
                    <div className="w-2 h-2 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                    <span className="text-gray-700">{opportunity}</span>
                  </div>
                ))}
              </div>
              
              <div className="mt-6">
                <h3 className="font-semibold text-gray-900 mb-3">Marketplace Activity</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div className="p-3 bg-yellow-50 rounded-lg">
                    <div className="text-lg font-bold text-yellow-600">
                      {ecosystemData.marketplaceActivity?.listingsActive || 150}
                    </div>
                    <div className="text-sm text-gray-600">Active Listings</div>
                  </div>
                  
                  <div className="p-3 bg-orange-50 rounded-lg">
                    <div className="text-lg font-bold text-orange-600">
                      {ecosystemData.marketplaceActivity?.transactionsCompleted || 45}
                    </div>
                    <div className="text-sm text-gray-600">Transactions</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'referral' && (
          <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
            <div className="flex items-center gap-3 mb-6">
              <Share2 className="w-8 h-8 text-green-600" />
              <h2 className="text-2xl font-bold text-gray-900">Referral Program</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6">
              {referralData && (
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Your Referral Stats</h3>
                  
                  <div className="grid grid-cols-2 gap-4 mb-6">
                    <div className="p-4 bg-green-50 rounded-lg">
                      <div className="text-2xl font-bold text-green-600">{referralData.currentReferrals}</div>
                      <div className="text-gray-600">Referrals</div>
                    </div>
                    
                    <div className="p-4 bg-purple-50 rounded-lg">
                      <div className="text-2xl font-bold text-purple-600">${referralData.rewardsEarned?.toFixed(2)}</div>
                      <div className="text-gray-600">Earned</div>
                    </div>
                  </div>
                  
                  <div className="mb-4">
                    <div className="text-sm font-medium text-gray-700 mb-2">Your Referral Link</div>
                    <input
                      type="text"
                      value={referralData.referralLink || ''}
                      readOnly
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50"
                    />
                  </div>
                  
                  <div className="mb-4">
                    <div className="text-sm font-medium text-gray-700 mb-2">Your Referral Code</div>
                    <div className="flex gap-2">
                      <input
                        type="text"
                        value={referralCode || referralData.referralCode || ''}
                        readOnly
                        className="flex-1 px-4 py-3 border border-gray-300 rounded-lg bg-gray-50"
                      />
                      <button
                        onClick={createReferralCode}
                        className="bg-green-600 hover:bg-green-700 text-white px-4 py-3 rounded-lg font-medium"
                      >
                        New Code
                      </button>
                    </div>
                  </div>
                  
                  <div>
                    <h4 className="font-semibold text-gray-900 mb-2">Program Benefits</h4>
                    <ul className="space-y-1">
                      {referralData.programBenefits?.map((benefit, index) => (
                        <li key={index} className="text-sm text-gray-700 flex items-start gap-2">
                          <div className="w-1.5 h-1.5 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                          {benefit}
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
              )}
              
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Process Referral</h3>
                
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Referral Code
                    </label>
                    <input
                      type="text"
                      value={referralCode}
                      onChange={(e) => setReferralCode(e.target.value)}
                      placeholder="Enter referral code"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      New User ID
                    </label>
                    <input
                      type="text"
                      value={newUserId}
                      onChange={(e) => setNewUserId(e.target.value)}
                      placeholder="Enter new user ID"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    />
                  </div>
                  
                  <button
                    onClick={processReferral}
                    className="w-full bg-green-600 hover:bg-green-700 text-white px-4 py-3 rounded-lg font-medium"
                  >
                    Process Referral
                  </button>
                  
                  {processSuccess !== null && (
                    <div className={`p-3 rounded-lg ${processSuccess ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {processSuccess ? 'Referral processed successfully!' : 'Invalid referral code'}
                    </div>
                  )}
                </div>
                
                {referralData?.performanceMetrics && (
                  <div className="mt-6">
                    <h4 className="font-semibold text-gray-900 mb-2">Performance Metrics</h4>
                    <div className="space-y-2">
                      {Object.entries(referralData.performanceMetrics).map(([key, value]) => (
                        <div key={key} className="flex justify-between text-sm">
                          <span className="text-gray-600 capitalize">{key.replace(/([A-Z])/g, ' $1')}:</span>
                          <span className="text-gray-900">
                            {typeof value === 'number' ? (typeof value === 'string' ? value : value.toFixed(2)) : value}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'marketplace' && marketplaceData && (
          <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
            <div className="flex items-center gap-3 mb-6">
              <ShoppingCart className="w-8 h-8 text-blue-600" />
              <h2 className="text-2xl font-bold text-gray-900">Premium Marketplace</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Featured Listings</h3>
                
                <div className="space-y-3">
                  {marketplaceData.featuredListings?.map((listing, index) => (
                    <div key={index} className="p-4 border border-blue-200 rounded-lg bg-blue-50">
                      <div className="flex justify-between items-start">
                        <div>
                          <div className="font-semibold text-blue-600 text-lg">{listing.domain}</div>
                          <div className="text-gray-600 text-sm">${listing.price?.toLocaleString()}</div>
                          <div className="text-gray-500 text-xs mt-1">{listing.reason}</div>
                        </div>
                        <button className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm">
                          Buy Now
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Market Trends</h3>
                
                <div className="space-y-4">
                  {marketplaceData.marketTrends && Object.entries(marketplaceData.marketTrends).map(([key, value]) => (
                    <div key={key} className="p-3 bg-gray-50 rounded-lg">
                      <div className="font-medium text-gray-900 capitalize">{key.replace(/([A-Z])/g, ' $1')}:</div>
                      <div className="text-gray-700">
                        {Array.isArray(value) ? value.join(', ') : typeof value === 'object' ? JSON.stringify(value) : value}
                      </div>
                    </div>
                  ))}
                  
                  <div className="mt-4">
                    <h4 className="font-semibold text-gray-900 mb-2">Available Domains</h4>
                    <div className="space-y-2">
                      {marketplaceData.availableDomains?.slice(0, 3).map((domain, index) => (
                        <div key={index} className="flex justify-between items-center p-2 border-b">
                          <span>{domain.domain}</span>
                          <span className="font-semibold">${domain.price?.toFixed(2)}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'marketing' && marketingData && (
          <div className="bg-white rounded-2xl shadow-xl p-6">
            <div className="flex items-center gap-3 mb-6">
              <BarChart3 className="w-8 h-8 text-indigo-600" />
              <h2 className="text-2xl font-bold text-gray-900">Marketing Insights</h2>
            </div>
            
            <div className="grid lg:grid-cols-3 gap-6">
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Traffic Analytics</h3>
                
                <div className="space-y-3">
                  {marketingData.trafficAnalytics && Object.entries(marketingData.trafficAnalytics).map(([key, value]) => (
                    <div key={key} className="flex justify-between">
                      <span className="text-gray-600 capitalize">{key.replace(/([A-Z])/g, ' $1')}:</span>
                      <span className="font-semibold">
                        {typeof value === 'number' ? value.toLocaleString() : value}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Conversion Metrics</h3>
                
                <div className="space-y-3">
                  {marketingData.conversionMetrics && Object.entries(marketingData.conversionMetrics).map(([key, value]) => (
                    <div key={key} className="flex justify-between">
                      <span className="text-gray-600 capitalize">{key.replace(/([A-Z])/g, ' $1')}:</span>
                      <span className="font-semibold">
                        {typeof value === 'number' ? (key.includes('rate') ? value.toFixed(2) + '%' : value.toLocaleString()) : value}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">ROI Analysis</h3>
                
                <div className="space-y-3">
                  {marketingData.roiCalculations && Object.entries(marketingData.roiCalculations).map(([key, value]) => (
                    <div key={key} className="flex justify-between">
                      <span className="text-gray-600 capitalize">{key.replace(/([A-Z])/g, ' $1')}:</span>
                      <span className="font-semibold">
                        {typeof value === 'number' ? (key.includes('percentage') ? value.toFixed(2) + '%' : '$' + value.toLocaleString()) : value}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
            
            <div className="mt-6 grid md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Campaign Performance</h3>
                
                <div className="space-y-3">
                  {marketingData.campaignPerformance && Object.entries(marketingData.campaignPerformance).map(([key, value]) => (
                    <div key={key} className="flex justify-between">
                      <span className="text-gray-600 capitalize">{key.replace(/([A-Z])/g, ' $1')}:</span>
                      <span className="font-semibold">
                        {typeof value === 'number' ? (key.includes('rate') ? value.toFixed(2) + '%' : value.toLocaleString()) : value}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Growth Predictions</h3>
                
                <div className="space-y-3">
                  {marketingData.growthPredictions && Object.entries(marketingData.growthPredictions).map(([key, value]) => (
                    <div key={key} className="flex justify-between">
                      <span className="text-gray-600 capitalize">{key.replace(/([A-Z])/g, ' $1')}:</span>
                      <span className="font-semibold">
                        {typeof value === 'number' ? (key.includes('rate') ? value.toFixed(2) + '%' : value.toLocaleString()) : value}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default EcosystemDashboard;