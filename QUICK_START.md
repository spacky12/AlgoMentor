# AlgoMentor - Quick Start Guide 🚀

## ⚡ 30-Second Start

```bash
# Terminal 1 - Backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend && npm start

# Open browser
http://localhost:3000
```

## 🎯 Test Accounts

### Teacher Account
- **Email**: teacher@algomentor.com
- **Password**: teacher123

### Student Account (Test)
- **Email**: teststudent@example.com
- **Password**: test123

## 📝 Create New Account

### Student Signup
1. Go to http://localhost:3000
2. Click "Sign Up"
3. Select "Student"
4. Fill form (password min 6 chars)
5. Done!

### Teacher Signup
1. Go to http://localhost:3000
2. Click "Sign Up"
3. Select "Teacher"
4. Fill form
5. Done!

## 🔑 API Quick Test

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teacher@algomentor.com","password":"teacher123"}'

# Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name":"John Doe",
    "email":"john@example.com",
    "password":"test123",
    "rollNumber":"2024001",
    "section":"A",
    "role":"STUDENT"
  }'
```

## 🌐 URLs

- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

## 📊 Features

✅ Student/Teacher signup & login
✅ Problem tracking
✅ HackerRank sync
✅ LeetCode sync
✅ Analytics dashboard
✅ Progress tracking
✅ Search & pagination

## 🐛 Troubleshooting

### Backend won't start
```bash
# Check Java version
java -version  # Need 17+

# Clean and rebuild
mvn clean install
```

### Frontend won't start
```bash
# Reinstall dependencies
cd frontend
rm -rf node_modules
npm install
```

### Can't login
- Check email/password
- Password must be 6+ characters
- Check backend is running

### Signup fails
- Email must be unique
- Roll number must be unique (students)
- Password min 6 characters

## 📚 Documentation

- **README.md** - Full setup guide
- **DEPLOYMENT.md** - Production deployment
- **SIGNUP_FIXED.md** - Signup details
- **FINAL_STATUS.md** - Current status

## 💡 Pro Tips

1. **Students**: Add your HackerRank/LeetCode usernames
2. **Teachers**: Use search to find students quickly
3. **Everyone**: Sync regularly for updated stats
4. **Developers**: Check logs for debugging

## 🎓 Next Steps

1. ✅ Create your account
2. ✅ Login and explore
3. ✅ Sync your coding profiles
4. ✅ View your analytics
5. ✅ Track your progress

## 🚀 Deploy to Production

```bash
# Using Docker
docker-compose up -d

# Manual deployment
See DEPLOYMENT.md
```

## 📞 Need Help?

1. Check FINAL_STATUS.md
2. Review error messages
3. Check browser console
4. Check backend logs
5. Review documentation

---

**Ready to go! Start at http://localhost:3000** 🎉
