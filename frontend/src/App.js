import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import AnalyticsDashboard from './components/AnalyticsDashboard';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Valid section ranges matching signup form
const VALID_SECTIONS = [
  ...Array.from({ length: 20 }, (_, i) => (601 + i).toString()),
  ...Array.from({ length: 20 }, (_, i) => (701 + i).toString()),
  ...Array.from({ length: 20 }, (_, i) => (801 + i).toString())
];

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

// Add response interceptor for better error handling
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

function App({ user, onLogout }) {
  const [students, setStudents] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [problems, setProblems] = useState([]);
  const [stats, setStats] = useState(null);
  const [showStudentForm, setShowStudentForm] = useState(false);
  const [showProblemForm, setShowProblemForm] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [editingProblem, setEditingProblem] = useState(null);
  const [fetchingHackerRank, setFetchingHackerRank] = useState(false);
  const [progressData, setProgressData] = useState([]);
  const [showProgressTable, setShowProgressTable] = useState(false);
  const [syncingProgress, setSyncingProgress] = useState({});

  const [studentForm, setStudentForm] = useState({
    name: '',
    email: '',
    rollNumber: '',
    section: '',
    group: '',
    leetcodeProfile: ''
  });

  const [problemForm, setProblemForm] = useState({
    title: '',
    platform: '',
    difficulty: 'Easy',
    status: 'solved'
  });

  useEffect(() => {
    loadStudents();
    loadStats();
    loadProgressData();
  }, []);

  useEffect(() => {
    if (selectedStudent) {
      loadProblems(selectedStudent.id);
    }
  }, [selectedStudent]);

  const loadStudents = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/students/me`);
      // For students, show only their own data
      setStudents([response.data]);
      if (!selectedStudent) {
        setSelectedStudent(response.data);
      }
    } catch (error) {
      console.error('Error loading student data:', error);
      if (error.response?.status === 403 || error.response?.status === 401) {
        alert('Session expired. Please login again.');
        if (onLogout) onLogout();
      }
    }
  };

  const loadProblems = async (studentId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/students/${studentId}/problems`);
      setProblems(response.data);
    } catch (error) {
      console.error('Error loading problems:', error);
      if (error.response?.status === 403) {
        alert('You can only view your own problems.');
      }
    }
  };

  const loadStats = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/stats`);
      setStats(response.data);
    } catch (error) {
      console.error('Error loading stats:', error);
    }
  };

  const loadProgressData = async () => {
    try {
      // Students can only see their own progress
      const response = await axios.get(`${API_BASE_URL}/progress/my-progress`);
      setProgressData([response.data]);

      // IMPORTANT: Also update selectedStudent with the progress data so analytics show on refresh
      if (response.data) {
        setSelectedStudent(prevStudent => ({
          ...prevStudent,
          ...response.data
        }));
      }
    } catch (error) {
      console.error('Error loading progress data:', error);
      if (error.response?.status === 403 || error.response?.status === 401) {
        alert('You can only view your own progress.');
      }
    }
  };

  const handleSyncProgress = async (studentId, platform) => {
    setSyncingProgress({ ...syncingProgress, [studentId]: true });
    try {
      const endpoint = platform === 'hackerrank'
        ? `${API_BASE_URL}/progress/hackerrank/${studentId}`
        : `${API_BASE_URL}/progress/leetcode/${studentId}`;

      const response = await axios.post(endpoint, {});
      await loadProgressData();
      await loadStats();

      // If we are showing progress for this student, update it
      if (response.data) {
        setProgressData([response.data]);
        if (selectedStudent?.id === studentId) {
          setSelectedStudent(response.data);
        }
      }
      alert(`Successfully synced ${platform} progress!`);
    } catch (error) {
      if (error.response?.status === 403) {
        alert('You can only sync your own progress.');
      } else {
        const errorMsg = error.response?.data?.message || error.message;
        alert(`Error syncing ${platform} progress: ${errorMsg}`);
      }
    } finally {
      setSyncingProgress({ ...syncingProgress, [studentId]: false });
    }
  };

  const handleCreateStudent = async (e) => {
    e.preventDefault();
    alert('Students cannot create other students. This feature is for teachers only.');
    setShowStudentForm(false);
  };

  const handleUpdateStudent = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`${API_BASE_URL}/students/${editingStudent.id}`, studentForm);
      setStudentForm({ name: '', email: '', rollNumber: '', section: '', hackerProfile: '', leetcodeProfile: '' });
      setEditingStudent(null);
      setShowStudentForm(false);
      loadStudents();
      loadStats();
      if (selectedStudent?.id === editingStudent.id) {
        setSelectedStudent({ ...selectedStudent, ...studentForm });
      }
    } catch (error) {
      alert('Error updating student: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleDeleteStudent = async (id) => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      try {
        await axios.delete(`${API_BASE_URL}/students/${id}`);
        loadStudents();
        loadStats();
        if (selectedStudent?.id === id) {
          setSelectedStudent(null);
          setProblems([]);
        }
      } catch (error) {
        alert('Error deleting student: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  const handleCreateProblem = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${API_BASE_URL}/students/${selectedStudent.id}/problems`, problemForm);
      setProblemForm({ title: '', platform: '', difficulty: 'Easy', status: 'solved' });
      setShowProblemForm(false);
      loadProblems(selectedStudent.id);
      loadStats();
    } catch (error) {
      if (error.response?.status === 403) {
        alert('You can only add problems to your own account.');
      } else {
        alert('Error creating problem: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  const handleUpdateProblem = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`${API_BASE_URL}/problems/${editingProblem.id}`, problemForm);
      setProblemForm({ title: '', platform: '', difficulty: 'Easy', status: 'solved' });
      setEditingProblem(null);
      setShowProblemForm(false);
      loadProblems(selectedStudent.id);
      loadStats();
    } catch (error) {
      alert('Error updating problem: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleDeleteProblem = async (id) => {
    if (window.confirm('Are you sure you want to delete this problem?')) {
      try {
        await axios.delete(`${API_BASE_URL}/problems/${id}`);
        loadProblems(selectedStudent.id);
        loadStats();
      } catch (error) {
        alert('Error deleting problem: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  const openEditStudent = (student) => {
    setEditingStudent(student);
    setStudentForm({
      name: student.name,
      email: student.email,
      rollNumber: student.rollNumber || '',
      section: student.section || '',
      leetcodeProfile: student.leetcodeProfile || ''
    });
    setShowStudentForm(true);
  };

  const openEditProblem = (problem) => {
    setEditingProblem(problem);
    setProblemForm({
      title: problem.title,
      platform: problem.platform,
      difficulty: problem.difficulty,
      status: problem.status
    });
    setShowProblemForm(true);
  };

  const cancelForm = () => {
    setShowStudentForm(false);
    setShowProblemForm(false);
    setEditingStudent(null);
    setEditingProblem(null);
    setStudentForm({ name: '', email: '', leetcodeProfile: '' });
    setProblemForm({ title: '', platform: '', difficulty: 'Easy', status: 'solved' });
  };

  const handleSyncData = async (studentId) => {
    if (!studentId) {
      alert('Please select a student first');
      return;
    }

    // Check if student has LeetCode profile configured
    const student = students.find(s => s.id === studentId);
    if (student && !student.leetcodeProfile) {
      alert('Please configure your LeetCode username in your profile first.\n\nClick "Edit Profile" to add your username.');
      return;
    }

    if (!window.confirm('This will fetch data from LeetCode. Continue?')) {
      return;
    }

    setFetchingHackerRank(true);
    try {
      const response = await axios.post(`${API_BASE_URL}/progress/sync-all/${studentId}`, {}, {
        timeout: 60000
      });

      const data = response.data;
      alert(`Successfully synced progress!\nTotal Problems: ${data.totalProblems}\nLeetCode: ${data.leetCodeTotal}`);

      if (selectedStudent?.id === studentId) {
        setSelectedStudent(data);
        loadProblems(studentId);
      }
      loadStats();
      loadStudents();
    } catch (error) {
      let errorMsg = 'Unknown error';

      if (error.code === 'ECONNABORTED') {
        errorMsg = 'Request timed out. The scraping process took too long.';
      } else if (error.code === 'ERR_NETWORK' || error.message === 'Network Error') {
        errorMsg = `Network Error: Cannot connect to backend.\n\nPlease ensure backend is running at ${API_BASE_URL}`;
      } else if (error.response) {
        errorMsg = error.response.data?.message || `Server error: ${error.response.status}`;
      } else {
        errorMsg = error.message;
      }

      console.error('Sync error:', error);
      alert('Error syncing data:\n' + errorMsg);
    } finally {
      setFetchingHackerRank(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
          <div>
            <h1>AlgoMentor</h1>
            <p>Student Dashboard - Problem Tracking System</p>
          </div>
          <button className="btn btn-secondary" onClick={onLogout}>
            Logout
          </button>
        </div>
      </header>

      <div className="container">
        <div className="stats-section">
          <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
            <button
              className={`btn ${!showProgressTable ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setShowProgressTable(false)}
            >
              Student Management
            </button>
            <button
              className={`btn ${showProgressTable ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => {
                setShowProgressTable(true);
                loadProgressData();
              }}
            >
              Progress Table
            </button>
          </div>

        </div>

        {showProgressTable ? (
          <div className="progress-table-container">
            <div className="progress-table-header">
              <h2>My Progress</h2>
              <button className="btn btn-primary" onClick={loadProgressData}>
                Refresh Data
              </button>
            </div>
            <div className="progress-table-wrapper">
              <table className="progress-table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Roll No.</th>
                    <th>Section</th>
                    <th colSpan="4">LeetCode</th>
                  </tr>
                  <tr className="sub-header">
                    <th></th>
                    <th></th>
                    <th></th>
                    <th>Total</th>
                    <th>Easy</th>
                    <th>Medium</th>
                    <th>Hard</th>
                  </tr>
                </thead>
                <tbody>
                  {progressData.length === 0 ? (
                    <tr>
                      <td colSpan="7" style={{ textAlign: 'center', padding: '2rem' }}>
                        No progress data available. Sync your profiles to see progress.
                      </td>
                    </tr>
                  ) : (
                    progressData.map((progress) => (
                      <tr key={progress.studentId}>
                        <td>{progress.studentName}</td>
                        <td>{progress.rollNumber || 'N/A'}</td>
                        <td>{progress.section || 'N/A'}</td>
                        <td className="number-cell total">{progress.leetCodeTotal}</td>
                        <td className="number-cell easy">{progress.leetCodeEasy}</td>
                        <td className="number-cell medium">{progress.leetCodeMedium}</td>
                        <td className="number-cell hard">{progress.leetCodeHard}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        ) : (
          <div className="main-content">
            <div className="students-panel">
              <div className="panel-header">
                <h2>My Profile</h2>
              </div>

              {showStudentForm && (
                <div className="form-card">
                  <h3>{editingStudent ? 'Edit Student' : 'Add New Student'}</h3>
                  <form onSubmit={editingStudent ? handleUpdateStudent : handleCreateStudent}>
                    <input
                      type="text"
                      placeholder="Name"
                      value={studentForm.name}
                      onChange={(e) => setStudentForm({ ...studentForm, name: e.target.value })}
                      required
                    />
                    <input
                      type="email"
                      placeholder="Email"
                      value={studentForm.email}
                      onChange={(e) => setStudentForm({ ...studentForm, email: e.target.value })}
                      required
                    />
                    <input
                      type="text"
                      placeholder="Roll Number"
                      value={studentForm.rollNumber}
                      onChange={(e) => setStudentForm({ ...studentForm, rollNumber: e.target.value })}
                      required
                    />
                    <select
                      name="section"
                      value={studentForm.section}
                      onChange={(e) => setStudentForm({ ...studentForm, section: e.target.value })}
                      required
                      style={{
                        width: '100%',
                        padding: '0.75rem 1rem',
                        border: '1px solid var(--border-light)',
                        borderRadius: 'var(--radius-md)',
                        marginBottom: 'var(--spacing-md)',
                        fontSize: '0.875rem',
                        background: 'var(--surface)',
                        fontFamily: 'Inter, sans-serif'
                      }}
                    >
                      <option value="">Select Section</option>
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
                    <input
                      type="text"
                      placeholder="LeetCode Username"
                      value={studentForm.leetcodeProfile}
                      onChange={(e) => setStudentForm({ ...studentForm, leetcodeProfile: e.target.value })}
                    />
                    <div className="form-actions">
                      <button type="submit" className="btn btn-primary">
                        {editingStudent ? 'Update' : 'Create'}
                      </button>
                      <button type="button" className="btn btn-secondary" onClick={cancelForm}>
                        Cancel
                      </button>
                    </div>
                  </form>
                </div>
              )}

              <div className="students-list">
                {students.length > 0 ? (
                  students.map(student => (
                    <div
                      key={student.id}
                      className={`student-card ${selectedStudent?.id === student.id ? 'selected' : ''}`}
                      onClick={() => setSelectedStudent(student)}
                    >
                      <div className="student-info">
                        <h3>{student.name}</h3>
                        <p className="student-email">{student.email}</p>
                        <p className="student-profile">Roll Number: {student.rollNumber || 'N/A'}</p>
                        <p className="student-profile">Section: {student.section || 'N/A'}</p>
                        {student.leetcodeProfile && (
                          <p className="student-profile">LeetCode: {student.leetcodeProfile}</p>
                        )}
                        <div className="problem-count-badge">
                          Problems: {student.problemCount}
                        </div>
                      </div>
                      <div className="student-actions">
                        <button
                          className="btn btn-small btn-secondary"
                          onClick={(e) => {
                            e.stopPropagation();
                            openEditStudent(student);
                          }}
                          style={{ marginRight: '0.5rem' }}
                        >
                          Edit Profile
                        </button>
                        <button
                          className="btn btn-small btn-fetch"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleSyncData(student.id);
                          }}
                          disabled={fetchingHackerRank}
                          title="Fetch data from HackerRank and LeetCode"
                        >
                          {fetchingHackerRank ? 'Fetching...' : '📥 Sync Data'}
                        </button>
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="empty-state">Loading your profile...</p>
                )}
              </div>
            </div>

            <div className="problems-panel">
              {selectedStudent ? (
                <>
                  <div className="panel-header">
                    <h2>Problems - {selectedStudent.name}</h2>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button
                        className="btn btn-fetch"
                        onClick={() => handleSyncData(selectedStudent.id)}
                        disabled={fetchingHackerRank}
                        title="Fetch problems from HackerRank and LeetCode"
                      >
                        {fetchingHackerRank ? '⏳ Syncing...' : '📥 Sync Progress'}
                      </button>
                    </div>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', alignItems: 'start' }}>
                    {/* Left Column: Visual Analytics */}
                    <AnalyticsDashboard problems={problems} studentData={selectedStudent} />

                    {/* Manual problem adding form removed as per requirements */}

                    {/* Right Column: Problems List */}
                    <div style={{
                      maxHeight: '450px',
                      overflowY: 'auto',
                      paddingRight: '0.5rem'
                    }}>
                      <h2 className="section-title" style={{ position: 'sticky', top: 0, background: 'white', zIndex: 10, paddingBottom: '0.5rem', marginBottom: '0.75rem' }}>📝 Problems</h2>
                      <div className="problems-list">
                        {problems.length === 0 ? (
                          <p className="empty-state">No problems yet. Add one to get started!</p>
                        ) : (
                          problems.map(problem => (
                            <div key={problem.id} className="problem-card">
                              <div className="problem-info">
                                <h3>{problem.title}</h3>
                                <div className="problem-details">
                                  <span className="badge badge-platform">{problem.platform}</span>
                                  <span className={`badge badge-difficulty ${problem.difficulty.toLowerCase()}`}>
                                    {problem.difficulty}
                                  </span>
                                  <span className={`badge badge-status ${problem.status}`}>
                                    {problem.status.replace('_', ' ')}
                                  </span>
                                </div>
                                <p className="problem-date">
                                  Solved: {new Date(problem.solvedAt).toLocaleDateString()}
                                </p>
                              </div>
                              {/* Manual edit/delete actions removed */}
                            </div>
                          ))
                        )}
                      </div>
                    </div>
                  </div>
                </>
              ) : (
                <div className="empty-panel">
                  <p>Select a student to view their problems</p>
                </div>
              )}
            </div>
          </div>
        )
        }
      </div >
    </div >
  );
}

export default App;
