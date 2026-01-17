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

        // Prefer stats from studentData if available (more accurate for total counts)
        if (studentData) {
            easy = (studentData.hackerRankEasy || 0) + (studentData.leetCodeEasy || 0);
            medium = (studentData.hackerRankMedium || 0) + (studentData.leetCodeMedium || 0);
            hard = (studentData.hackerRankHard || 0) + (studentData.leetCodeHard || 0);
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
            { name: 'Easy', value: easy, color: '#00b894' },   // Green
            { name: 'Medium', value: medium, color: '#fdcb6e' }, // Orange
            { name: 'Hard', value: hard, color: '#d63031' }     // Red
        ].filter(d => d.value > 0);
    }, [problems, studentData]);

    // 2. Platform Breakdown (Bar Chart)
    const platformData = useMemo(() => {
        let hr = 0, lc = 0;

        if (studentData) {
            hr = studentData.hackerRankTotal || 0;
            lc = studentData.leetCodeTotal || 0;
        }

        return [
            { name: 'HackerRank', count: hr, fill: '#2ecc71' },
            { name: 'LeetCode', count: lc, fill: '#f39c12' }
        ];
    }, [studentData]);

    // 3. Activity Timeline (Area Chart) - Mocked logic or derived from solvedAt
    // Since we might not have historical data for every problem from the API stats,
    // we will try to use the 'problems' list 'solvedAt' if available, otherwise 
    // we might generate a dummy trend for visual appeal or show "Recent Activity"
    const activityData = useMemo(() => {
        // Group problems by date
        const dates = {};
        const sortedProblems = [...problems].sort((a, b) => new Date(a.solvedAt) - new Date(b.solvedAt));

        if (sortedProblems.length === 0) return [];

        sortedProblems.forEach(p => {
            if (!p.solvedAt) return;
            const date = p.solvedAt.split('T')[0]; // YYYY-MM-DD
            dates[date] = (dates[date] || 0) + 1;
        });

        return Object.keys(dates).map(date => ({
            date,
            solved: dates[date]
        }));
    }, [problems]);

    // 4. Last 10 Submissions
    const latestSubmissions = useMemo(() => {
        return [...problems]
            .sort((a, b) => new Date(b.solvedAt) - new Date(a.solvedAt))
            .slice(0, 10);
    }, [problems]);

    // Check if we have any data to show
    const hasData = (studentData && (studentData.hackerRankTotal > 0 || studentData.leetCodeTotal > 0)) || problems.length > 0;

    if (!hasData) {
        return (
            <div className="analytics-dashboard">
                <h2 className="section-title">📊 visual analytics</h2>
                <div className="empty-chart-state" style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8', background: 'white', borderRadius: '0.75rem', border: '1px solid #e2e8f0' }}>
                    <p style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>📉 No data to visualize yet</p>
                    <p style={{ fontSize: '0.9rem' }}>Click <strong>"Sync Progress"</strong> to fetch your latest stats from HackerRank and LeetCode.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="analytics-dashboard">
            <h2 className="section-title">📊 visual analytics</h2>

            <div className="charts-container">
                {/* Card 1: Difficulty Distribution */}
                <div className="chart-card">
                    <h3>Difficulty Mastery</h3>
                    <div className="chart-wrapper">
                        <ResponsiveContainer width="100%" height={250}>
                            <PieChart>
                                <Pie
                                    data={difficultyData}
                                    cx="50%"
                                    cy="50%"
                                    innerRadius={60}
                                    outerRadius={80}
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

                {/* Card 2: Platform Breakdown */}
                <div className="chart-card">
                    <h3>Platform Dominance</h3>
                    <div className="chart-wrapper">
                        <ResponsiveContainer width="100%" height={250}>
                            <BarChart data={platformData}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                <XAxis dataKey="name" />
                                <YAxis />
                                <Tooltip cursor={{ fill: 'transparent' }} />
                                <Bar dataKey="count" radius={[10, 10, 0, 0]}>
                                    {platformData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={entry.fill} />
                                    ))}
                                </Bar>
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Card 3: Activity Timeline (Only show if we have data) */}
                {activityData.length > 0 && (
                    <div className="chart-card full-width">
                        <h3>Solving Velocity</h3>
                        <div className="chart-wrapper">
                            <ResponsiveContainer width="100%" height={250}>
                                <AreaChart data={activityData}>
                                    <defs>
                                        <linearGradient id="colorSolved" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="#8884d8" stopOpacity={0.8} />
                                            <stop offset="95%" stopColor="#8884d8" stopOpacity={0} />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                    <XAxis dataKey="date" />
                                    <YAxis />
                                    <Tooltip />
                                    <Area type="monotone" dataKey="solved" stroke="#8884d8" fillOpacity={1} fill="url(#colorSolved)" />
                                </AreaChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                )}
            </div>

            {/* Section 2: Recent Submissions Table */}
            {latestSubmissions.length > 0 && (
                <div className="submissions-section" style={{ marginTop: '2rem' }}>
                    <h3 style={{ marginBottom: '1rem', color: '#1e293b', fontSize: '1.1rem', fontWeight: '600' }}>🕒 Recent 10 Submissions</h3>
                    <div className="table-responsive" style={{ background: 'white', borderRadius: '0.75rem', border: '1px solid #e2e8f0', overflow: 'hidden' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                            <thead style={{ background: '#f8fafc', borderBottom: '1px solid #e2e8f0' }}>
                                <tr>
                                    <th style={{ padding: '1rem' }}>Problem Title</th>
                                    <th style={{ padding: '1rem' }}>Platform</th>
                                    <th style={{ padding: '1rem' }}>Difficulty</th>
                                    <th style={{ padding: '1rem' }}>Solved Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                {latestSubmissions.map((sub, idx) => (
                                    <tr key={sub.id || idx} style={{ borderBottom: idx === latestSubmissions.length - 1 ? 'none' : '1px solid #f1f5f9' }}>
                                        <td style={{ padding: '1rem', fontWeight: '500' }}>{sub.title}</td>
                                        <td style={{ padding: '1rem' }}>
                                            <span className="badge badge-platform" style={{ padding: '0.25rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', background: '#e0f2fe', color: '#0369a1' }}>
                                                {sub.platform}
                                            </span>
                                        </td>
                                        <td style={{ padding: '1rem' }}>
                                            <span className={`badge badge-difficulty ${sub.difficulty.toLowerCase()}`}>
                                                {sub.difficulty}
                                            </span>
                                        </td>
                                        <td style={{ padding: '1rem', color: '#64748b', fontSize: '0.85rem' }}>
                                            {new Date(sub.solvedAt).toLocaleDateString()}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AnalyticsDashboard;
