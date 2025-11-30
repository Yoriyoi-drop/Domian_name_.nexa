import React, { useState, useEffect } from 'react';
import { Button } from '@/shared/components/ui/button';
import { Input } from '@/shared/components/ui/input';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from '@/shared/components/ui/card';
import { useParams, useNavigate } from 'react-router-dom';
import useApi from '@/shared/hooks/useApi';

const UserEditPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { get, put } = useApi();
  
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    role: '',
    status: '',
    profile: {
      firstName: '',
      lastName: '',
      phone: '',
      address: ''
    }
  });
  
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  // Load user data on component mount
  useEffect(() => {
    const fetchUser = async () => {
      try {
        // In a real app, this would fetch from the API
        // const { data } = await get(`/api/v1/users/${id}`);
        // For this example, we'll use mock data
        const mockUserData = {
          id: 1,
          username: 'johndoe',
          email: 'john@example.com',
          role: 'Admin',
          status: 'Active',
          profile: {
            firstName: 'John',
            lastName: 'Doe',
            phone: '+1 (555) 123-4567',
            address: '123 Main St, Anytown, USA'
          }
        };
        
        setFormData(mockUserData);
      } catch (error) {
        console.error('Error fetching user:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.includes('.')) {
      // Handle nested properties (profile.firstName, profile.lastName, etc.)
      const [parent, child] = name.split('.');
      setFormData(prev => ({
        ...prev,
        [parent]: {
          ...prev[parent],
          [child]: value
        }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    
    try {
      // In a real app, this would update via API
      // await put({ url: `/api/v1/users/${id}`, data: formData });
      
      // For this example, we'll just navigate back after a delay
      setTimeout(() => {
        navigate(`/users/${id}`);
      }, 1000);
    } catch (error) {
      console.error('Error updating user:', error);
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Edit User</h1>
      
      <Card>
        <CardHeader>
          <CardTitle>Edit User Information</CardTitle>
          <CardDescription>Update the user's account details</CardDescription>
        </CardHeader>
        <form onSubmit={handleSubmit}>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <label htmlFor="username" className="text-sm font-medium">Username</label>
                <Input
                  id="username"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                />
              </div>
              <div className="space-y-2">
                <label htmlFor="email" className="text-sm font-medium">Email</label>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                />
              </div>
              <div className="space-y-2">
                <label htmlFor="role" className="text-sm font-medium">Role</label>
                <select
                  id="role"
                  name="role"
                  value={formData.role}
                  onChange={handleChange}
                  className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <option value="User">User</option>
                  <option value="Admin">Admin</option>
                  <option value="Moderator">Moderator</option>
                </select>
              </div>
              <div className="space-y-2">
                <label htmlFor="status" className="text-sm font-medium">Status</label>
                <select
                  id="status"
                  name="status"
                  value={formData.status}
                  onChange={handleChange}
                  className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <option value="Active">Active</option>
                  <option value="Inactive">Inactive</option>
                </select>
              </div>
            </div>
            
            <div className="mt-6">
              <h3 className="text-lg font-medium mb-4">Profile Information</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label htmlFor="profile.firstName" className="text-sm font-medium">First Name</label>
                  <Input
                    id="profile.firstName"
                    name="profile.firstName"
                    value={formData.profile.firstName}
                    onChange={handleChange}
                  />
                </div>
                <div className="space-y-2">
                  <label htmlFor="profile.lastName" className="text-sm font-medium">Last Name</label>
                  <Input
                    id="profile.lastName"
                    name="profile.lastName"
                    value={formData.profile.lastName}
                    onChange={handleChange}
                  />
                </div>
                <div className="space-y-2">
                  <label htmlFor="profile.phone" className="text-sm font-medium">Phone</label>
                  <Input
                    id="profile.phone"
                    name="profile.phone"
                    value={formData.profile.phone}
                    onChange={handleChange}
                  />
                </div>
                <div className="space-y-2">
                  <label htmlFor="profile.address" className="text-sm font-medium">Address</label>
                  <Input
                    id="profile.address"
                    name="profile.address"
                    value={formData.profile.address}
                    onChange={handleChange}
                  />
                </div>
              </div>
            </div>
          </CardContent>
          <CardFooter className="flex justify-between">
            <Button variant="outline" type="button" onClick={() => navigate(`/users/${id}`)}>
              Cancel
            </Button>
            <Button type="submit" disabled={saving}>
              {saving ? 'Saving...' : 'Save Changes'}
            </Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default UserEditPage;