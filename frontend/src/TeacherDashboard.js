import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

const API_BASE_URL = 'http://localhost:8080/api';

// Configure axios to include JWT token in all requests
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

function TeacherDashboard({ onLogout }) {
  const [sections, setSections] = useState([]);
  const [selectedSection, setSelectedSection] = useState('');
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [syncing, setSyncing] = useState(false);

  useEffect(() => {
    loadSections();
  }, []);

  useEffect(() => {
    if (selectedSection) {
      loadStudentsBySection(selectedSection);
    }
  }, [selectedSection]);

  const loadSections = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/teacher/sections`);
      setSections(Array.from(response.data).sort());
    } catch (error) {
      console.error('Error loading sections:', error);
      if (error.response?.status === 403 || error.response?.status === 401) {
        const confirmLogout = window.confirm('Access denied. Teacher access required. \n\nYour session might be expired or you may have an incorrect role. \n\nWould you like to logout and login again?');
        if (confirmLogout && onLogout) {
          onLogout();
        } else if (!onLogout) {
          alert('Access denied. Teacher access required.');
        }
      }
    }
  };

  const loadStudentsBySection = async (section) => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/teacher/section/${encodeURIComponent(section)}/students`);
      setStudents(response.data);
    } catch (error) {
      console.error('Error loading students:', error);
      if (error.response?.status === 403 || error.response?.status === 401) {
        alert('Access denied. Teacher access required.');
      } else {
        alert('Error loading students: ' + (error.response?.data?.message || error.message));
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSyncAll = async () => {
    if (!selectedSection) {
      alert('Please select a section first');
      return;
    }

    if (!window.confirm(`Sync all students in section ${selectedSection}?`)) {
      return;
    }

    setSyncing(true);
    try {
      const response = await axios.post(`${API_BASE_URL}/teacher/section/${encodeURIComponent(selectedSection)}/sync-all`, {});
      alert(response.data.message);
      loadStudentsBySection(selectedSection);
    } catch (error) {
      if (error.response?.status === 403 || error.response?.status === 401) {
        alert('Access denied. Teacher access required.');
      } else {
        alert('Error syncing: ' + (error.response?.data?.message || error.message));
      }
    } finally {
      setSyncing(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
          <div>
            <h1>AlgoMentor - Teacher Dashboard</h1>
            <p>Student Progress Tracking System</p>
          </div>
          <button className="btn btn-secondary" onClick={onLogout}>
            Logout
          </button>
        </div>
      </header>

      <div className="container">
        <div className="section-selector">
          <h2>Select Section</h2>
          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
            <select
              value={selectedSection}
              onChange={(e) => setSelectedSection(e.target.value)}
              className="section-select"
              style={{ padding: '0.75rem', fontSize: '1rem', borderRadius: '8px', border: '2px solid #e0e0e0', minWidth: '200px' }}
            >
              <option value="">-- Select Section --</option>
              {sections.map(section => (
                <option key={section} value={section}>{section}</option>
              ))}
            </select>
            {selectedSection && (
              <button
                className="btn btn-primary"
                onClick={handleSyncAll}
                disabled={syncing}
              >
                {syncing ? 'Syncing...' : '🔄 Sync All Students'}
              </button>
            )}
          </div>
        </div>

        {selectedSection && (
          <div className="progress-table-container">
            <div className="progress-table-header">
              <h2>Students in Section: {selectedSection}</h2>
              <button className="btn btn-primary" onClick={() => loadStudentsBySection(selectedSection)}>
                Refresh
              </button>
            </div>

            {loading ? (
              <div style={{ textAlign: 'center', padding: '2rem' }}>Loading...</div>
            ) : students.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '2rem' }}>
                No students found in this section.
              </div>
            ) : (
              <div className="progress-table-wrapper">
                <table className="progress-table">
                  <thead>
                    <tr>
                      <th>Roll No.</th>
                      <th>Name</th>
                      <th>Email</th>
                      <th colSpan="4">HackerRank</th>
                      <th colSpan="4">LeetCode</th>
                      <th colSpan="4">Total</th>
                    </tr>
                    <tr className="sub-header">
                      <th></th>
                      <th></th>
                      <th></th>
                      <th>Total</th>
                      <th>Easy</th>
                      <th>Medium</th>
                      <th>Hard</th>
                      <th>Total</th>
                      <th>Easy</th>
                      <th>Medium</th>
                      <th>Hard</th>
                      <th>Total</th>
                      <th>Easy</th>
                      <th>Medium</th>
                      <th>Hard</th>
                    </tr>
                  </thead>
                  <tbody>
                    {students.map((student) => (
                      <tr key={student.studentId}>
                        <td>{student.rollNumber || 'N/A'}</td>
                        <td>{student.studentName}</td>
                        <td>{student.email}</td>
                        <td className="number-cell">{student.hackerRankTotal}</td>
                        <td className="number-cell easy">{student.hackerRankEasy}</td>
                        <td className="number-cell medium">{student.hackerRankMedium}</td>
                        <td className="number-cell hard">{student.hackerRankHard}</td>
                        <td className="number-cell">{student.leetCodeTotal}</td>
                        <td className="number-cell easy">{student.leetCodeEasy}</td>
                        <td className="number-cell medium">{student.leetCodeMedium}</td>
                        <td className="number-cell hard">{student.leetCodeHard}</td>
                        <td className="number-cell total">{student.totalProblems}</td>
                        <td className="number-cell easy">{student.easyProblems}</td>
                        <td className="number-cell medium">{student.mediumProblems}</td>
                        <td className="number-cell hard">{student.hardProblems}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default TeacherDashboard;
