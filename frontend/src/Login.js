import React, { useState } from 'react';
import axios from 'axios';
import './Login.css';

const API_BASE_URL = 'http://localhost:8080/api';

function Login({ onLogin }) {
  const [isSignup, setIsSignup] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    name: '',
    rollNumber: '',
    hackerrankProfile: '',
    leetcodeProfile: '',
    section: '',
    role: 'STUDENT'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isSignup) {
        const response = await axios.post(`${API_BASE_URL}/auth/signup`, formData);
        if (response.data.token) {
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('role', response.data.role);
          localStorage.setItem('userId', response.data.userId);
          localStorage.setItem('email', response.data.email);
          onLogin(response.data);
        } else {
          setError(response.data.message || 'Signup failed');
        }
      } else {
        const response = await axios.post(`${API_BASE_URL}/auth/login`, {
          email: formData.email,
          password: formData.password
        });
        if (response.data.token) {
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('role', response.data.role);
          localStorage.setItem('userId', response.data.userId);
          localStorage.setItem('email', response.data.email);
          onLogin(response.data);
        } else {
          setError(response.data.message || 'Login failed');
        }
      }
    } catch (error) {
      setError(error.response?.data || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1>AlgoMentor</h1>
        <h2>{isSignup ? (formData.role === 'TEACHER' ? 'Teacher Sign Up' : 'Student Sign Up') : 'Login'}</h2>

        {error && <div className="error-message">
          {typeof error === 'string' ? error : (error.message || 'An error occurred')}
          {error.errors && (
            <ul style={{ textAlign: 'left', fontSize: '0.8rem', marginTop: '0.5rem' }}>
              {error.errors.map((err, i) => <li key={i}>{err.defaultMessage || err}</li>)}
            </ul>
          )}
        </div>}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ marginRight: '1rem' }}>Role:</label>
            <select
              name="role"
              value={formData.role || 'STUDENT'}
              onChange={handleChange}
              style={{ padding: '0.5rem', borderRadius: '4px' }}
            >
              <option value="STUDENT">Student</option>
              <option value="TEACHER">Teacher</option>
            </select>
          </div>

          {isSignup && (
            <>
              <input
                type="text"
                name="name"
                placeholder="Full Name"
                value={formData.name}
                onChange={handleChange}
                required
              />
              {(formData.role === 'STUDENT' || !formData.role) && (
                <>
                  <input
                    type="text"
                    name="rollNumber"
                    placeholder="Roll Number"
                    value={formData.rollNumber}
                    onChange={handleChange}
                    required
                  />
                  <input
                    type="text"
                    name="section"
                    placeholder="Section (e.g., A, B, CSE-A)"
                    value={formData.section}
                    onChange={handleChange}
                    required
                  />
                  <input
                    type="text"
                    name="hackerrankProfile"
                    placeholder="HackerRank Username (optional)"
                    value={formData.hackerrankProfile}
                    onChange={handleChange}
                  />
                  <input
                    type="text"
                    name="leetcodeProfile"
                    placeholder="LeetCode Username (optional)"
                    value={formData.leetcodeProfile}
                    onChange={handleChange}
                  />
                </>
              )}
            </>
          )}

          <input
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            required
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required
          />

          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? 'Please wait...' : (isSignup ? 'Sign Up' : 'Login')}
          </button>
        </form>

        <p className="toggle-text">
          {isSignup ? (
            <>Already have an account? <button onClick={() => setIsSignup(false)} className="link-btn">Login</button></>
          ) : (
            <>Don't have an account? <button onClick={() => setIsSignup(true)} className="link-btn">Sign Up</button></>
          )}
        </p>
      </div>
    </div>
  );
}

export default Login;
