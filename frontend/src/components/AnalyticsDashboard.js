import React, { useMemo } from 'react';
import {
    PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
    BarChart, Bar, XAxis, YAxis, CartesianGrid,
    AreaChart, Area
} from 'recharts';

const AnalyticsDashboard = ({ problems, studentData }) => {

    // 1. Difficulty Distribution (Pie Chart)
    const difficultyData = useMemo(() => {
        let easy = 0, medium = 0, hard = 0;

        // Use stats from studentData if available
        if (studentData) {
            easy = studentData.leetCodeEasy || 0;
            medium = studentData.leetCodeMedium || 0;
            hard = studentData.leetCodeHard || 0;
        }

        // Fallback to counting problems list if stats are 0 (e.g. before sync)
        if (easy === 0 && medium === 0 && hard === 0 && problems.length > 0) {
            problems.forEach(p => {
                const diff = p.difficulty ? p.difficulty.toLowerCase() : 'unknown';
                if (diff === 'easy') easy++;
                else if (diff === 'medium') medium++;
                else if (diff === 'hard') hard++;
            });
        }

        return [
            { name: 'Easy', value: easy, color: '#6b7280' },      // Medium gray
            { name: 'Medium', value: medium, color: '#9ca3af' },   // Light gray
            { name: 'Hard', value: hard, color: '#1f2937' }        // Dark gray
        ].filter(d => d.value > 0);
    }, [problems, studentData]);



    // 4. Last 10 Submissions
    const latestSubmissions = useMemo(() => {
        return [...problems]
            .sort((a, b) => new Date(b.solvedAt) - new Date(a.solvedAt))
            .slice(0, 10);
    }, [problems]);

    // Check if we have any data to show
    const hasData = (studentData && studentData.leetCodeTotal > 0) || problems.length > 0;

    if (!hasData) {
        return (
            <div className="analytics-dashboard">
                <h2 className="section-title">📊 visual analytics</h2>
                <div className="empty-chart-state" style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8', background: 'white', borderRadius: '0.75rem', border: '1px solid #e2e8f0' }}>
                    <p style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>📉 No data to visualize yet</p>
                    <p style={{ fontSize: '0.9rem' }}>Click <strong>"Sync Progress"</strong> to fetch your latest stats from LeetCode.</p>
                </div>
            </div>
        );
    }

    return (
        <div style={{
            maxHeight: '450px',
            overflowY: 'auto',
            paddingRight: '0.5rem'
        }}>
            <div className="analytics-dashboard">
                <h2 className="section-title" style={{ position: 'sticky', top: 0, background: 'white', zIndex: 10, paddingBottom: '0.5rem' }}>📊 Visual Analytics</h2>

                {/* Stat Cards */}
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(4, 1fr)',
                    gap: '0.5rem',
                    marginBottom: '1rem'
                }}>
                    <div style={{
                        background: 'linear-gradient(135deg, hsl(0, 0%, 20%) 0%, hsl(0, 0%, 30%) 100%)',
                        padding: '0.75rem',
                        borderRadius: '0.5rem',
                        color: 'white',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.125rem' }}>
                            {studentData?.leetCodeTotal || 0}
                        </div>
                        <div style={{ fontSize: '0.7rem', opacity: 0.9 }}>Total Solved</div>
                    </div>

                    <div style={{
                        background: 'white',
                        padding: '0.75rem',
                        borderRadius: '0.5rem',
                        border: '1.5px solid hsl(0, 0%, 85%)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.125rem', color: 'hsl(0, 0%, 40%)' }}>
                            {studentData?.leetCodeEasy || 0}
                        </div>
                        <div style={{ fontSize: '0.7rem', color: 'hsl(0, 0%, 50%)' }}>Easy</div>
                    </div>

                    <div style={{
                        background: 'white',
                        padding: '0.75rem',
                        borderRadius: '0.5rem',
                        border: '1.5px solid hsl(0, 0%, 70%)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.125rem', color: 'hsl(0, 0%, 35%)' }}>
                            {studentData?.leetCodeMedium || 0}
                        </div>
                        <div style={{ fontSize: '0.7rem', color: 'hsl(0, 0%, 50%)' }}>Medium</div>
                    </div>

                    <div style={{
                        background: 'white',
                        padding: '0.75rem',
                        borderRadius: '0.5rem',
                        border: '1.5px solid hsl(0, 0%, 40%)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.125rem', color: 'hsl(0, 0%, 20%)' }}>
                            {studentData?.leetCodeHard || 0}
                        </div>
                        <div style={{ fontSize: '0.7rem', color: 'hsl(0, 0%, 50%)' }}>Hard</div>
                    </div>
                </div>

                {/* Difficulty Distribution Chart */}
                <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '1rem' }}>
                    {/* Difficulty Distribution Pie Chart */}
                    <div className="chart-card">
                        <h3>Difficulty Distribution</h3>
                        <div className="chart-wrapper">
                            <ResponsiveContainer width="100%" height={200}>
                                <PieChart>
                                    <Pie
                                        data={difficultyData}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={50}
                                        outerRadius={70}
                                        paddingAngle={5}
                                        dataKey="value"
                                    >
                                        {difficultyData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={entry.color} />
                                        ))}
                                    </Pie>
                                    <Tooltip />
                                    <Legend />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* Summary Stats */}
                    <div className="chart-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '1.5rem' }}>
                        <h3 style={{ marginBottom: '1rem', fontSize: '1rem' }}>Progress Summary</h3>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <span style={{ fontSize: '0.95rem', color: 'hsl(0, 0%, 40%)' }}>Easy Problems:</span>
                                <span style={{ fontSize: '1.25rem', fontWeight: '600', color: 'hsl(0, 0%, 45%)' }}>
                                    {studentData?.leetCodeEasy || 0}
                                </span>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <span style={{ fontSize: '0.95rem', color: 'hsl(0, 0%, 35%)' }}>Medium Problems:</span>
                                <span style={{ fontSize: '1.25rem', fontWeight: '600', color: 'hsl(0, 0%, 35%)' }}>
                                    {studentData?.leetCodeMedium || 0}
                                </span>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <span style={{ fontSize: '0.95rem', color: 'hsl(0, 0%, 20%)' }}>Hard Problems:</span>
                                <span style={{ fontSize: '1.25rem', fontWeight: '600', color: 'hsl(0, 0%, 20%)' }}>
                                    {studentData?.leetCodeHard || 0}
                                </span>
                            </div>
                            <div style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                                marginTop: '0.5rem',
                                paddingTop: '1rem',
                                borderTop: '2px solid hsl(0, 0%, 90%)'
                            }}>
                                <span style={{ fontSize: '1.1rem', fontWeight: '600', color: 'hsl(0, 0%, 20%)' }}>Total:</span>
                                <span style={{ fontSize: '1.5rem', fontWeight: '700', color: 'hsl(0, 0%, 15%)' }}>
                                    {studentData?.leetCodeTotal || 0}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Recent Activity Table */}
                <div className="chart-card" style={{ padding: '1rem' }}>
                    <h3 style={{ marginBottom: '1rem', fontSize: '1rem' }}>Recent Activity (Last 10)</h3>
                    <div style={{ overflowX: 'auto' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
                            <thead>
                                <tr style={{ borderBottom: '2px solid #f1f5f9', textAlign: 'left' }}>
                                    <th style={{ padding: '0.5rem', color: '#64748b' }}>Date</th>
                                    <th style={{ padding: '0.5rem', color: '#64748b' }}>Title</th>
                                    <th style={{ padding: '0.5rem', color: '#64748b' }}>Platform</th>
                                    <th style={{ padding: '0.5rem', color: '#64748b' }}>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {latestSubmissions.length > 0 ? (
                                    latestSubmissions.map((prob, idx) => (
                                        <tr key={idx} style={{ borderBottom: '1px solid #f8fafc' }}>
                                            <td style={{ padding: '0.5rem', color: '#334155' }}>
                                                {new Date(prob.solvedAt).toLocaleDateString()}
                                            </td>
                                            <td style={{ padding: '0.5rem', fontWeight: '500', color: '#0f172a' }}>
                                                {prob.title}
                                            </td>
                                            <td style={{ padding: '0.5rem' }}>
                                                <span className={`badge badge-platform`} style={{ fontSize: '0.7rem' }}>
                                                    {prob.platform}
                                                </span>
                                            </td>
                                            <td style={{ padding: '0.5rem' }}>
                                                <span
                                                    style={{
                                                        display: 'inline-block',
                                                        width: '8px',
                                                        height: '8px',
                                                        borderRadius: '50%',
                                                        backgroundColor: prob.status === 'solved' ? '#10b981' : '#f59e0b',
                                                        marginRight: '6px'
                                                    }}
                                                ></span>
                                                <span style={{ color: prob.status === 'solved' ? '#059669' : '#d97706' }}>
                                                    {prob.status === 'solved' ? 'Solved' : 'Attempted'}
                                                </span>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4" style={{ padding: '1rem', textAlign: 'center', color: '#94a3b8' }}>
                                            No recent activity found.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default AnalyticsDashboard;
