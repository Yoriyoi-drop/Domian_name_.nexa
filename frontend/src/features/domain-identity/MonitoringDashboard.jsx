import React, { useState, useEffect } from 'react';
import { Activity, BarChart3, TrendingUp, Zap, Shield, Database, Globe, Clock } from 'lucide-react';
import { useApi } from '../../core/hooks/useApi';

const MonitoringDashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [businessMetrics, setBusinessMetrics] = useState(null);
  const [innovationIndicators, setInnovationIndicators] = useState(null);
  const [healthStatus, setHealthStatus] = useState({});
  const [loading, setLoading] = useState(false);
  const { get, post } = useApi();

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // Get monitoring dashboard
      const dashboardResponse = await get('/api/v1/monitoring/dashboard');
      setDashboardData(dashboardResponse.data);
      
      // Get business metrics
      const metricsResponse = await get('/api/v1/monitoring/business-metrics');
      setBusinessMetrics(metricsResponse.data);
      
      // Get innovation indicators
      const innovationResponse = await get('/api/v1/monitoring/innovation-indicators');
      setInnovationIndicators(innovationResponse.data);
      
      // Extract health status
      if (dashboardResponse.data.healthStatus) {
        setHealthStatus(dashboardResponse.data.healthStatus);
      }
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const logCustomMetric = async () => {
    try {
      await post('/api/v1/monitoring/business-metric', null, {
        params: {
          metricName: 'user.engagement',
          value: 85.5,
          tags: ['department:marketing', 'campaign:holiday2025']
        }
      });
      alert('Custom metric logged successfully!');
    } catch (error) {
      console.error('Error logging custom metric:', error);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const healthStatusColor = healthStatus.overallHealth === 'GREEN' ? 'text-green-600' : 
                           healthStatus.overallHealth === 'YELLOW' ? 'text-yellow-600' : 'text-red-600';

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 p-4 md:p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="flex items-center justify-center gap-3 mb-4">
            <Activity className="w-10 h-10 text-blue-600" />
            <h1 className="text-3xl md:text-4xl font-bold text-gray-900">
              Monitoring & Innovation Dashboard
            </h1>
          </div>
          <p className="text-lg text-gray-600 max-w-3xl mx-auto">
            Real-time monitoring and observability for your .nexa domain infrastructure. 
            Experience the future-first approach with advanced metrics and innovation indicators.
          </p>
        </div>

        {/* Refresh Button */}
        <div className="flex justify-center mb-8">
          <button
            onClick={fetchDashboardData}
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center gap-2"
          >
            {loading ? (
              <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
            ) : (
              <>
                <Activity className="w-5 h-5" />
                Refresh Dashboard
              </>
            )}
          </button>
        </div>

        {dashboardData && (
          <div className="grid lg:grid-cols-3 gap-8 mb-8">
            {/* Health Status Card */}
            <div className="bg-white rounded-2xl shadow-xl p-6 lg:col-span-1">
              <div className="flex items-center gap-3 mb-4">
                <Shield className="w-8 h-8 text-green-600" />
                <h2 className="text-2xl font-bold text-gray-900">System Health</h2>
              </div>
              
              <div className={`text-2xl font-bold mb-4 ${healthStatusColor}`}>
                Status: {healthStatus.overallHealth || 'UNKNOWN'}
              </div>
              
              <div className="space-y-3">
                {healthStatus.database && (
                  <div className="flex justify-between items-center p-3 bg-green-50 rounded-lg">
                    <span>Database</span>
                    <span className={`font-semibold ${healthStatus.database === 'UP' ? 'text-green-600' : 'text-red-600'}`}>
                      {healthStatus.database}
                    </span>
                  </div>
                )}
                {healthStatus.redis && (
                  <div className="flex justify-between items-center p-3 bg-green-50 rounded-lg">
                    <span>Redis Cache</span>
                    <span className={`font-semibold ${healthStatus.redis === 'UP' ? 'text-green-600' : 'text-red-600'}`}>
                      {healthStatus.redis}
                    </span>
                  </div>
                )}
                {healthStatus.rabbitmq && (
                  <div className="flex justify-between items-center p-3 bg-green-50 rounded-lg">
                    <span>Message Queue</span>
                    <span className={`font-semibold ${healthStatus.rabbitmq === 'UP' ? 'text-green-600' : 'text-red-600'}`}>
                      {healthStatus.rabbitmq}
                    </span>
                  </div>
                )}
                <div className="flex justify-between items-center p-3 bg-blue-50 rounded-lg">
                  <span>Uptime</span>
                  <span className="font-semibold text-blue-600">
                    {dashboardData.performanceIndicators?.uptime || '99.99'}%
                  </span>
                </div>
              </div>
            </div>

            {/* Performance Indicators */}
            <div className="bg-white rounded-2xl shadow-xl p-6 lg:col-span-2">
              <div className="flex items-center gap-3 mb-4">
                <BarChart3 className="w-8 h-8 text-purple-600" />
                <h2 className="text-2xl font-bold text-gray-900">Performance Indicators</h2>
              </div>
              
              <div className="grid md:grid-cols-2 gap-4">
                <div className="p-4 bg-blue-50 rounded-lg">
                  <div className="flex items-center gap-2 mb-2">
                    <Clock className="w-5 h-5 text-blue-600" />
                    <span className="font-semibold">Avg Response Time</span>
                  </div>
                  <div className="text-2xl font-bold text-gray-900">
                    {dashboardData.performanceIndicators?.avgResponseTime || '245'}ms
                  </div>
                </div>
                
                <div className="p-4 bg-green-50 rounded-lg">
                  <div className="flex items-center gap-2 mb-2">
                    <TrendingUp className="w-5 h-5 text-green-600" />
                    <span className="font-semibold">Requests/Sec</span>
                  </div>
                  <div className="text-2xl font-bold text-gray-900">
                    {dashboardData.systemMetrics?.requestsPerSecond || '45'}
                  </div>
                </div>
                
                <div className="p-4 bg-yellow-50 rounded-lg">
                  <div className="flex items-center gap-2 mb-2">
                    <Database className="w-5 h-5 text-yellow-600" />
                    <span className="font-semibold">CPU Usage</span>
                  </div>
                  <div className="text-2xl font-bold text-gray-900">
                    {dashboardData.systemMetrics?.cpuUsage || '65'}%
                  </div>
                </div>
                
                <div className="p-4 bg-indigo-50 rounded-lg">
                  <div className="flex items-center gap-2 mb-2">
                    <Globe className="w-5 h-5 text-indigo-600" />
                    <span className="font-semibold">Active Sessions</span>
                  </div>
                  <div className="text-2xl font-bold text-gray-900">
                    {dashboardData.systemMetrics?.activeConnections || '128'}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {businessMetrics && (
          <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
            <div className="flex items-center gap-3 mb-6">
              <TrendingUp className="w-8 h-8 text-green-600" />
              <h2 className="text-2xl font-bold text-gray-900">Business Metrics</h2>
            </div>
            
            <div className="grid md:grid-cols-3 gap-6">
              <div className="text-center p-4 bg-blue-50 rounded-lg">
                <div className="text-3xl font-bold text-blue-600">
                  {Math.round(businessMetrics.userRegistrations || 0)}
                </div>
                <div className="text-gray-600">User Registrations</div>
              </div>
              
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <div className="text-3xl font-bold text-green-600">
                  {Math.round(businessMetrics.userLogins || 0)}
                </div>
                <div className="text-gray-600">User Logins</div>
              </div>
              
              <div className="text-center p-4 bg-purple-50 rounded-lg">
                <div className="text-3xl font-bold text-purple-600">
                  {(businessMetrics.errorRate || 0.12).toFixed(2)}%
                </div>
                <div className="text-gray-600">Error Rate</div>
              </div>
            </div>

            <div className="mt-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-3">Business KPIs</h3>
              <div className="grid md:grid-cols-2 gap-4">
                {businessMetrics.businessKpis && Object.entries(businessMetrics.businessKpis).map(([key, value]) => (
                  <div key={key} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                    <span className="text-gray-700 capitalize">{key.replace(/([A-Z])/g, ' $1')}</span>
                    <span className="font-semibold text-gray-900">
                      {typeof value === 'number' ? value.toFixed(2) : value}
                      {typeof value === 'number' && key.includes('rate') ? '%' : ''}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {innovationIndicators && (
          <div className="bg-white rounded-2xl shadow-xl p-6 mb-8">
            <div className="flex items-center gap-3 mb-6">
              <Zap className="w-8 h-8 text-yellow-600" />
              <h2 className="text-2xl font-bold text-gray-900">Innovation Indicators</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Innovation Scores</h3>
                <div className="space-y-3">
                  <div className="flex justify-between items-center">
                    <span>Domain Innovation</span>
                    <div className="flex items-center gap-2">
                      <div className="w-32 bg-gray-200 rounded-full h-2">
                        <div 
                          className="bg-yellow-500 h-2 rounded-full" 
                          style={{ width: `${innovationIndicators.domainInnovationScore}%` }}
                        ></div>
                      </div>
                      <span className="text-sm font-semibold">{innovationIndicators.domainInnovationScore}/100</span>
                    </div>
                  </div>
                  
                  <div className="flex justify-between items-center">
                    <span>Technology Stack</span>
                    <div className="flex items-center gap-2">
                      <div className="w-32 bg-gray-200 rounded-full h-2">
                        <div 
                          className="bg-green-500 h-2 rounded-full" 
                          style={{ width: `${innovationIndicators.technologyStackModern}%` }}
                        ></div>
                      </div>
                      <span className="text-sm font-semibold">{innovationIndicators.technologyStackModern}/100</span>
                    </div>
                  </div>
                  
                  <div className="flex justify-between items-center">
                    <span>Performance Optimization</span>
                    <div className="flex items-center gap-2">
                      <div className="w-32 bg-gray-200 rounded-full h-2">
                        <div 
                          className="bg-blue-500 h-2 rounded-full" 
                          style={{ width: `${innovationIndicators.performanceOptimization}%` }}
                        ></div>
                      </div>
                      <span className="text-sm font-semibold">{innovationIndicators.performanceOptimization}/100</span>
                    </div>
                  </div>
                  
                  <div className="flex justify-between items-center">
                    <span>Security Innovation</span>
                    <div className="flex items-center gap-2">
                      <div className="w-32 bg-gray-200 rounded-full h-2">
                        <div 
                          className="bg-red-500 h-2 rounded-full" 
                          style={{ width: `${innovationIndicators.securityInnovation}%` }}
                        ></div>
                      </div>
                      <span className="text-sm font-semibold">{innovationIndicators.securityInnovation}/100</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Competitive Advantages</h3>
                <ul className="space-y-2">
                  {innovationIndicators.competitiveAdvantages?.map((advantage, index) => (
                    <li key={index} className="flex items-start gap-2 text-sm text-gray-700">
                      <div className="w-1.5 h-1.5 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                      {advantage}
                    </li>
                  ))}
                </ul>
                
                <button
                  onClick={logCustomMetric}
                  className="mt-4 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg font-medium text-sm"
                >
                  Log Custom Metric
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Tracing Information */}
        {dashboardData?.tracingInfo && (
          <div className="bg-white rounded-2xl shadow-xl p-6">
            <div className="flex items-center gap-3 mb-6">
              <Activity className="w-8 h-8 text-indigo-600" />
              <h2 className="text-2xl font-bold text-gray-900">Distributed Tracing</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Tracing Metrics</h3>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span>Active Traces</span>
                    <span className="font-semibold">{dashboardData.tracingInfo.activeTraces}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Sampling Rate</span>
                    <span className="font-semibold">{dashboardData.tracingInfo.traceSamplingRate * 100}%</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Avg Trace Duration</span>
                    <span className="font-semibold">{dashboardData.tracingInfo.averageTraceDuration}ms</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Success Rate</span>
                    <span className="font-semibold">{dashboardData.tracingInfo.traceSuccessRate}%</span>
                  </div>
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Performance Issues</h3>
                <div className="space-y-2">
                  {dashboardData.tracingInfo.topSlowEndpoints?.map((endpoint, index) => (
                    <div key={index} className="flex items-center gap-2 p-2 bg-red-50 rounded">
                      <div className="w-2 h-2 bg-red-500 rounded-full"></div>
                      <span className="text-sm text-gray-700">{endpoint}</span>
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

export default MonitoringDashboard;