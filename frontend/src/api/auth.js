import client from './client';

export const authApi = {
  signup: (data) => client.post('/auth/signup', data),
  login: (data) => client.post('/auth/login', data),
  logout: () => client.post('/auth/logout'),
  getMe: () => client.get('/auth/me'),
};

export const userApi = {
  updateProfile: (data) => client.put('/users/me', data),
  changePassword: (data) => client.put('/users/me/password', data),
};
