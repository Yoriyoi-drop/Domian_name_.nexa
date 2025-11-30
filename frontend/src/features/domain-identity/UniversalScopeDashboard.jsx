import React, { useState } from 'react';
import { Globe, Settings, Link, Zap, Cpu, Code, ShoppingBag, Users, BarChart3 } from 'lucide-react';
import { useApi } from '../../core/hooks/useApi';

const UniversalScopeDashboard = () => {
  const [domainName, setDomainName] = useState('myproject.nexa');
  const [subdomain, setSubdomain] = useState('');
  const [purpose, setPurpose] = useState('app');
  const [longUrl, setLongUrl] = useState('');
  const [customAlias, setCustomAlias] = useState('');
  const [scopeInfo, setScopeInfo] = useState(null);
  const [configuration, setConfiguration] = useState(null);
  const [nexaLink, setNexaLink] = useState(null);
  const [recommendedSubdomains, setRecommendedSubdomains] = useState([]);
  const [activeTab, setActiveTab] = useState('overview');
  const { get, post } = useApi();

  const fetchScopeInfo = async () => {
    try {
      const response = await get(`/api/v1/universal-scope/info/${domainName}`);
      setScopeInfo(response.data);
      const subdomainsResponse = await get(`/api/v1/universal-scope/recommended-subdomains/${domainName}`);
      setRecommendedSubdomains(subdomainsResponse.data);
    } catch (error) {
      console.error('Error fetching scope info:', error);
    }
  };

  const configureSubdomain = async () => {
    if (!subdomain || !purpose) return;
    
    try {
      const response = await post('/api/v1/universal-scope/configure-subdomain', null, {
        params: {
          domainName,
          subdomain,
          purpose
        }
      });
      setConfiguration(response.data);
    } catch (error) {
      console.error('Error configuring subdomain:', error);
    }
  };

  const generateNexaLink = async () => {
    if (!longUrl) return;
    
    try {
      const response = await post('/api/v1/universal-scope/nexa-link', null, {
        params: {
          longUrl,
          customAlias: customAlias || undefined
        }
      });
      setNexaLink(response.data);
    } catch (error) {
      console.error('Error generating Nexa-Link:', error);
    }
  };

  React.useEffect(() => {
    if (domainName) {
      fetchScopeInfo();
    }
  }, [domainName]);

  const purposeOptions = [
    { value: 'api', label: 'API Service', icon: Code },
    { value: 'app', label: 'Application', icon: Cpu },
    { value: 'marketing', label: 'Marketing', icon: BarChart3 },
    { value: 'blog', label: 'Blog/CMS', icon: Users },
    { value: 'admin', label: 'Admin Panel', icon: Settings },
    { value: 'shop', label: 'E-commerce', icon: ShoppingBag }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-indigo-50 p-4 md:p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="flex items-center justify-center gap-3 mb-4">
            <Globe className="w-10 h-10 text-indigo-600" />
            <h1 className="text-3xl md:text-4xl font-bold text-gray-900">
              Universal Scope Dashboard
            </h1>
          </div>
          <p className="text-lg text-gray-600 max-w-3xl mx-auto">
            Experience the universal flexibility of .nexa - unlike niche TLDs like .app, .dev, or .tech,
            .nexa works seamlessly for any business aspect without implying specific limitations.
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
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              />
            </div>
            <button
              onClick={fetchScopeInfo}
              className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center gap-2 mt-6 md:mt-0"
            >
              <Globe className="w-5 h-5" />
              Analyze Domain
            </button>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-2 mb-8 overflow-x-auto">
          <button
            onClick={() => setActiveTab('overview')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap ${
              activeTab === 'overview'
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Overview
          </button>
          <button
            onClick={() => setActiveTab('subdomain')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap ${
              activeTab === 'subdomain'
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Subdomain Config
          </button>
          <button
            onClick={() => setActiveTab('nexa-link')}
            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap ${
              activeTab === 'nexa-link'
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Nexa-Link
          </button>
        </div>

        {activeTab === 'overview' && scopeInfo && (
          <div className="grid lg:grid-cols-2 gap-8 mb-8">
            {/* Universal Scope Benefits */}
            <div className="bg-white rounded-2xl shadow-xl p-6">
              <div className="flex items-center gap-3 mb-4">
                <Zap className="w-8 h-8 text-yellow-600" />
                <h2 className="text-2xl font-bold text-gray-900">Universal Scope Benefits</h2>
              </div>
              
              <div className="space-y-3">
                {scopeInfo.benefits?.map((benefit, index) => (
                  <div key={index} className="flex items-start gap-3 p-3 bg-green-50 rounded-lg">
                    <div className="w-2 h-2 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                    <span className="text-gray-700">{benefit}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Scope Categories */}
            <div className="bg-white rounded-2xl shadow-xl p-6">
              <div className="flex items-center gap-3 mb-4">
                <Settings className="w-8 h-8 text-blue-600" />
                <h2 className="text-2xl font-bold text-gray-900">Scope Categories</h2>
              </div>
              
              <div className="grid grid-cols-2 gap-3">
                {scopeInfo.scopeCategories?.map((category, index) => (
                  <div key={index} className="p-3 bg-indigo-50 rounded-lg text-center">
                    <span className="text-gray-700 font-medium">{category}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Subdomain Examples */}
            <div className="bg-white rounded-2xl shadow-xl p-6 lg:col-span-2">
              <div className="flex items-center gap-3 mb-4">
                <Globe className="w-8 h-8 text-purple-600" />
                <h2 className="text-2xl font-bold text-gray-900">Subdomain Examples</h2>
              </div>
              
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b">
                      <th className="text-left py-3 px-4">Subdomain</th>
                      <th className="text-left py-3 px-4">Purpose</th>
                    </tr>
                  </thead>
                  <tbody>
                    {Object.entries(scopeInfo.subdomainExamples || {}).map(([subdomain, purpose], index) => (
                      <tr key={index} className="border-b">
                        <td className="py-3 px-4 font-mono text-blue-600">{subdomain}</td>
                        <td className="py-3 px-4 text-gray-700">{purpose}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Comparison with Niche TLDs */}
            <div className="bg-white rounded-2xl shadow-xl p-6 lg:col-span-2">
              <div className="flex items-center gap-3 mb-4">
                <BarChart3 className="w-8 h-8 text-green-600" />
                <h2 className="text-2xl font-bold text-gray-900">Comparison with Niche TLDs</h2>
              </div>
              
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b">
                      <th className="text-left py-3 px-4">TLD</th>
                      <th className="text-left py-3 px-4">Scope</th>
                      <th className="text-left py-3 px-4">Perception</th>
                      <th className="text-left py-3 px-4">Flexibility</th>
                      <th className="text-left py-3 px-4">Limitation</th>
                    </tr>
                  </thead>
                  <tbody>
                    {scopeInfo.comparisonWithNicheTlds && Object.entries(scopeInfo.comparisonWithNicheTlds).map(([tld, details], index) => (
                      <tr key={index} className="border-b">
                        <td className={`py-3 px-4 font-semibold ${tld === '.nexa' ? 'text-indigo-600' : 'text-gray-700'}`}>
                          {tld}
                        </td>
                        <td className="py-3 px-4">{details.Scope}</td>
                        <td className="py-3 px-4">{details.Perception}</td>
                        <td className="py-3 px-4">{details.Flexibility}</td>
                        <td className={`py-3 px-4 ${tld === '.nexa' ? 'text-green-600' : 'text-red-600'}`}>
                          {details.Limitation}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'subdomain' && (
          <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
            <div className="flex items-center gap-3 mb-6">
              <Settings className="w-8 h-8 text-indigo-600" />
              <h2 className="text-2xl font-bold text-gray-900">Subdomain Configuration</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6 mb-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Subdomain
                </label>
                <input
                  type="text"
                  value={subdomain}
                  onChange={(e) => setSubdomain(e.target.value)}
                  placeholder="e.g., api, app, admin"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Purpose
                </label>
                <select
                  value={purpose}
                  onChange={(e) => setPurpose(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                >
                  {purposeOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            
            <button
              onClick={configureSubdomain}
              className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-lg font-medium transition-colors"
            >
              Configure Subdomain
            </button>
            
            {configuration && (
              <div className="mt-6 p-6 bg-gray-50 rounded-lg">
                <h3 className="text-xl font-bold text-gray-900 mb-4">Configuration for {configuration.subdomain}</h3>
                
                <div className="grid md:grid-cols-2 gap-6">
                  <div>
                    <h4 className="font-semibold text-gray-700 mb-2">Configuration Settings</h4>
                    <div className="space-y-2">
                      {Object.entries(configuration.configuration || {}).map(([key, value]) => (
                        <div key={key} className="flex justify-between text-sm">
                          <span className="text-gray-600">{key}:</span>
                          <span className="text-gray-900">
                            {typeof value === 'boolean' ? value.toString() : value}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                  
                  <div>
                    <h4 className="font-semibold text-gray-700 mb-2">Recommended Settings</h4>
                    <ul className="space-y-1">
                      {configuration.recommendedSettings?.map((setting, index) => (
                        <li key={index} className="text-sm text-gray-600 flex items-start gap-2">
                          <div className="w-1.5 h-1.5 bg-indigo-500 rounded-full mt-2 flex-shrink-0"></div>
                          {setting}
                        </li>
                      ))}
                    </ul>
                    
                    <h4 className="font-semibold text-gray-700 mt-4 mb-2">Benefits</h4>
                    <ul className="space-y-1">
                      {configuration.benefits?.map((benefit, index) => (
                        <li key={index} className="text-sm text-gray-600 flex items-start gap-2">
                          <div className="w-1.5 h-1.5 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                          {benefit}
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'nexa-link' && (
          <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
            <div className="flex items-center gap-3 mb-6">
              <Link className="w-8 h-8 text-indigo-600" />
              <h2 className="text-2xl font-bold text-gray-900">Nexa-Link URL Shortener</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6 mb-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Long URL
                </label>
                <input
                  type="url"
                  value={longUrl}
                  onChange={(e) => setLongUrl(e.target.value)}
                  placeholder="https://example.com/very/long/url"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Custom Alias (optional)
                </label>
                <input
                  type="text"
                  value={customAlias}
                  onChange={(e) => setCustomAlias(e.target.value)}
                  placeholder="e.g., promo, download"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                />
              </div>
            </div>
            
            <button
              onClick={generateNexaLink}
              className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-lg font-medium transition-colors"
            >
              Generate Nexa-Link
            </button>
            
            {nexaLink && (
              <div className="mt-6 p-6 bg-green-50 rounded-lg">
                <h3 className="text-xl font-bold text-gray-900 mb-2">Your Nexa-Link</h3>
                <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
                  <a 
                    href={nexaLink.originalUrl} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="text-blue-600 hover:text-blue-800 break-all"
                  >
                    {nexaLink.originalUrl}
                  </a>
                  <div className="text-gray-500">→</div>
                  <a 
                    href={nexaLink.shortUrl} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="text-indigo-600 font-mono hover:text-indigo-800 break-all"
                  >
                    {nexaLink.shortUrl}
                  </a>
                </div>
                <p className="text-sm text-gray-600 mt-2">
                  Created: {new Date(nexaLink.createdAt).toLocaleString()}
                  {nexaLink.expiresAt && ` | Expires: ${new Date(nexaLink.expiresAt).toLocaleString()}`}
                </p>
              </div>
            )}
          </div>
        )}

        {/* Recommended Subdomains */}
        {recommendedSubdomains && recommendedSubdomains.length > 0 && (
          <div className="bg-white rounded-2xl shadow-xl p-6">
            <div className="flex items-center gap-3 mb-4">
              <Globe className="w-8 h-8 text-green-600" />
              <h2 className="text-2xl font-bold text-gray-900">Recommended Subdomains</h2>
            </div>
            
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-3">
              {recommendedSubdomains.map((subdomain, index) => (
                <div key={index} className="p-4 bg-blue-50 rounded-lg font-mono text-sm">
                  {subdomain}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UniversalScopeDashboard;