import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor to handle errors (401)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      // In a real app with React Router, we might emit an event or use a history object
      // to redirect, but reloading or letting the AuthContext handle the state change is also valid.
      // For this implementation, the AuthContext usually syncs with localStorage.
      if (!window.location.hash.includes('login')) {
         window.location.hash = '#/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;