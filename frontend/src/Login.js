import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Login.css';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Auto-detect current batch year (academic year: July-June)
const getCurrentBatch = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth(); // 0-11

  // If before July (month 6), use previous year for batch
  const academicYear = month < 6 ? year - 1 : year;
  return `${String(academicYear).slice(-2)}BCS`;
};

// Generate valid section options: 601-620, 701-720, 801-820
const VALID_SECTIONS = [
  ...Array.from({ length: 20 }, (_, i) => (601 + i).toString()),
  ...Array.from({ length: 20 }, (_, i) => (701 + i).toString()),
  ...Array.from({ length: 20 }, (_, i) => (801 + i).toString())
];

function Login({ onLogin }) {
  const [isSignup, setIsSignup] = useState(false);
  const batchPrefix = '24BCS'; // Fixed batch prefix
  const [studentNumber, setStudentNumber] = useState('');

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    name: '',
    rollNumber: '',
    leetcodeProfile: '',
    section: '',
    group: '',
    role: 'STUDENT'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Update rollNumber when batch or student number changes
  useEffect(() => {
    if (studentNumber && formData.role === 'STUDENT') {
      setFormData(prev => ({
        ...prev,
        rollNumber: `${batchPrefix}${studentNumber}`
      }));
    }
  }, [batchPrefix, studentNumber, formData.role]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const handleStudentNumberChange = (e) => {
    const value = e.target.value.replace(/\D/g, ''); // Only digits
    if (value.length <= 6) {
      setStudentNumber(value);
      setError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validate student number for student role during signup
    if (isSignup && formData.role === 'STUDENT') {
      if (!studentNumber || studentNumber.length !== 6) {
        setError('Student number must be exactly 6 digits');
        return;
      }
      if (!formData.section) {
        setError('Please select a section');
        return;
      }
    }

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
      console.error('Auth error:', error);
      if (error.response?.data) {
        if (error.response.data.message) {
          setError(error.response.data.message);
        } else if (typeof error.response.data === 'object') {
          const errorMessages = Object.entries(error.response.data)
            .map(([field, message]) => `${field}: ${message}`)
            .join(', ');
          setError(errorMessages);
        } else {
          setError(error.response.data);
        }
      } else if (error.message) {
        setError(error.message);
      } else {
        setError('An error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1>AlgoMentor</h1>
        <h2>{isSignup ? 'Student Sign Up' : 'Login'}</h2>

        {error && <div className="error-message">
          {typeof error === 'string' ? error : (error.message || 'An error occurred')}
          {error.errors && (
            <ul style={{ textAlign: 'left', fontSize: '0.8rem', marginTop: '0.5rem' }}>
              {error.errors.map((err, i) => <li key={i}>{err.defaultMessage || err}</li>)}
            </ul>
          )}
        </div>}

        <form onSubmit={handleSubmit}>


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
                  {/* Simplified Roll Number Input */}
                  <div style={{ position: 'relative', marginBottom: '1.25rem' }}>
                    <input
                      type="text"
                      value={studentNumber}
                      onChange={handleStudentNumberChange}
                      placeholder="Student Number (e.g., 601001)"
                      required
                      maxLength={6}
                      pattern="\d{6}"
                      style={{ paddingLeft: '5rem', width: '100%', boxSizing: 'border-box' }}
                    />
                    <span style={{
                      position: 'absolute',
                      left: '1.125rem',
                      top: '50%',
                      transform: 'translateY(-50%)',
                      fontSize: '1rem',
                      color: 'hsl(0, 0%, 30%)',
                      fontWeight: '500',
                      pointerEvents: 'none'
                    }}>
                      {batchPrefix}
                    </span>
                  </div>

                  {/* Section and Group */}
                  <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '0.75rem' }}>
                    <select
                      name="section"
                      value={formData.section}
                      onChange={handleChange}
                      required
                      style={{
                        width: '100%',
                        padding: '0.875rem 1.125rem',
                        border: '1px solid hsl(0, 0%, 90%)',
                        borderRadius: '0.75rem',
                        fontSize: '1rem',
                        background: 'white'
                      }}
                    >
                      <option value="">Section</option>
                      <optgroup label="600 Series">
                        {VALID_SECTIONS.slice(0, 20).map(sec => (
                          <option key={sec} value={sec}>{sec}</option>
                        ))}
                      </optgroup>
                      <optgroup label="700 Series">
                        {VALID_SECTIONS.slice(20, 40).map(sec => (
                          <option key={sec} value={sec}>{sec}</option>
                        ))}
                      </optgroup>
                      <optgroup label="800 Series">
                        {VALID_SECTIONS.slice(40, 60).map(sec => (
                          <option key={sec} value={sec}>{sec}</option>
                        ))}
                      </optgroup>
                    </select>

                    <select
                      name="group"
                      value={formData.group}
                      onChange={handleChange}
                      required
                      style={{
                        width: '100%',
                        padding: '0.875rem 1.125rem',
                        border: '1px solid hsl(0, 0%, 90%)',
                        borderRadius: '0.75rem',
                        fontSize: '1rem',
                        background: 'white'
                      }}
                    >
                      <option value="">Group</option>
                      <option value="A">A</option>
                      <option value="B">B</option>
                    </select>
                  </div>

                  <input
                    type="text"
                    name="leetcodeProfile"
                    placeholder="LeetCode Username (required)"
                    value={formData.leetcodeProfile}
                    onChange={handleChange}
                    required
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
