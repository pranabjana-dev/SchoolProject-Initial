# 🏫 School Management System
> Admissions & Fee Management — Spring Boot + Vanilla JS

---

## ✅ What This App Does

| Feature | Description |
|---|---|
| **Programme Management** | Define age-based programmes (Toddler → UKG) |
| **Fee Structure** | Set Fixed + Session fees per programme per academic year |
| **Discount Catalog** | Early Bird (fixed/%) and Loyalty discounts |
| **Fee Calculator** | Enter DOB + Joining date → get prorated fees, programme recommendation, and payment schedule |
| **Payment Schedule** | Booking amount + 3 installments (40/30/30 split) |
| **Print / PDF** | Browser print-to-PDF for student fee sheets |

### Business Logic Implemented
- **Govt Recommended Programme** → age calculated as of June 1 of the academic year start
- **Actual Age Programme** → age calculated from DOB to today's date
- **Proration** → from joining month to March 31 (whole month counted, even for mid-month joining)
- **Academic Year** → June–March (e.g., June 2024 → March 2025 = "2024-25")
- **Payment Schedule** → Booking (Fixed) + Inst1 (40%) + Inst2 (30%) + Inst3 (30%) of variable portion

---

## 🗂️ Project Structure

```
school-management/
├── src/main/java/com/school/
│   ├── SchoolManagementApplication.java      ← Entry point
│   ├── config/CorsConfig.java                ← CORS settings
│   ├── model/
│   │   ├── Program.java
│   │   ├── FeeStructure.java
│   │   ├── Discount.java
│   │   ├── FeeCalculationRequest.java
│   │   └── FeeCalculationResult.java
│   ├── storage/JsonStorageService.java        ← JSON file storage
│   ├── service/
│   │   ├── ProgramService.java
│   │   ├── FeeService.java
│   │   ├── DiscountService.java
│   │   └── CalculatorService.java             ← All business logic
│   └── controller/
│       ├── ProgramController.java             ← GET/POST/PUT/DELETE /api/programs
│       ├── FeeController.java                 ← GET/POST/PUT/DELETE /api/fees
│       ├── DiscountController.java            ← GET/POST/PUT/DELETE /api/discounts
│       └── CalculatorController.java          ← POST /api/calculate
├── src/main/resources/
│   ├── application.properties
│   └── static/index.html                      ← Full frontend (single file)
├── Dockerfile
├── render.yaml
└── pom.xml
```

---

## 🚀 Option A — Run Locally (VS Code)

### Prerequisites
- Java 17+ → https://adoptium.net/
- Maven 3.8+ → https://maven.apache.org/download.cgi
- (or use the Maven wrapper if you add one)

### Steps

```bash
# 1. Clone / open the folder in VS Code
cd school-management

# 2. Build the project
mvn clean package -DskipTests

# 3. Run
java -jar target/school-management-1.0.0.jar

# 4. Open in browser
open http://localhost:8080
```

Data is saved to: `~/school-data/` (your home directory)

### VS Code Extensions (recommended)
- **Extension Pack for Java** (Microsoft) — for Java language support
- **Spring Boot Dashboard** — run/debug Spring Boot apps
- **REST Client** — to test APIs directly

---

## ☁️ Option B — Deploy Free on Render.com (Recommended)

**Render.com** gives you a free web service with HTTPS, custom domain support, and auto-deploy from GitHub.

### Step 1 — Push to GitHub
```bash
# Initialize git (if not already)
git init
git add .
git commit -m "Initial commit: School Management System"

# Create a GitHub repo, then push
git remote add origin https://github.com/YOUR_USERNAME/school-management.git
git push -u origin main
```

### Step 2 — Create Render Account
1. Go to → https://render.com
2. Sign up with GitHub (free)

### Step 3 — Deploy
1. Click **New → Web Service**
2. Connect your GitHub repo
3. Render will auto-detect the `Dockerfile`
4. Settings:
   - **Name:** school-management
   - **Plan:** Free
   - **Branch:** main
5. Click **Create Web Service**

### Step 4 — Access Your App
- Render gives you a URL like: `https://school-management-xxxx.onrender.com`
- First boot takes ~2 minutes (free tier cold start)

> ⚠️ **Free Tier Note:** On Render's free plan, the filesystem resets on each deploy. Your configured fees/discounts will reset. Options:
> - **Option 1:** Upgrade to paid ($7/mo) and uncomment the `disk:` section in `render.yaml`
> - **Option 2:** Use Railway.app (free $5/month credit, no cold starts)

---

## ☁️ Option C — Deploy on Railway.app

Railway gives $5 free credit per month — enough for this app.

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Deploy from project folder
railway init
railway up
```

Railway auto-detects Docker and deploys. Get your URL from the Railway dashboard.

---

## 🔌 API Reference

All endpoints are under `/api/`:

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/programs` | List all programmes |
| POST | `/api/programs` | Create programme |
| PUT | `/api/programs/{id}` | Update programme |
| DELETE | `/api/programs/{id}` | Delete programme |
| GET | `/api/fees` | List all fee structures |
| POST | `/api/fees` | Create fee structure |
| PUT | `/api/fees/{id}` | Update fee structure |
| DELETE | `/api/fees/{id}` | Delete fee structure |
| GET | `/api/discounts` | List all discounts |
| POST | `/api/discounts` | Create discount |
| PATCH | `/api/discounts/{id}/toggle` | Toggle active |
| DELETE | `/api/discounts/{id}` | Delete discount |
| POST | `/api/calculate` | **Calculate fees** |

### Sample: Fee Calculation Request
```json
POST /api/calculate
{
  "studentName": "Ananya Sharma",
  "dateOfBirth": "2022-03-15",
  "joiningDate": "2024-10-01",
  "discountIds": ["<discount-uuid>"],
  "useGovtRecommended": true
}
```

---

## 🔧 Making Changes in VS Code

### Change port
Edit `src/main/resources/application.properties`:
```properties
server.port=9090
```

### Add a new field to FeeStructure
1. Add field + getter/setter in `FeeStructure.java`
2. Handle in `FeeService.java` if needed
3. Add input in the modal in `index.html`

### Change installment percentages
Edit `CalculatorService.java`:
```java
result.setInstallment1(round2(remaining * 0.40)); // change 0.40
result.setInstallment2(round2(remaining * 0.30)); // change 0.30
result.setInstallment3(round2(remaining * 0.30)); // change 0.30
```

### Change school name
In `index.html`, find:
```html
<div class="header-title">School Management System</div>
```
Replace with your school name.

---

## 📦 Building a Production JAR

```bash
mvn clean package -DskipTests
# Output: target/school-management-1.0.0.jar
java -jar target/school-management-1.0.0.jar
```

---

## 🗄️ Data Storage

Data is stored as JSON files in `DATA_DIR` (default: `~/school-data/`):
- `programs.json` — Programme definitions
- `fees.json` — Fee structures
- `discounts.json` — Discount catalog

To backup, simply copy the `school-data/` folder.
To restore, copy it back and restart the app.

---

## 🔮 Suggested Future Enhancements

- [ ] Student registration & enrollment tracking
- [ ] Payment recording against installments
- [ ] Email/WhatsApp fee quote generation
- [ ] Multi-school / multi-branch support
- [ ] PostgreSQL/MySQL database migration
- [ ] Role-based access (Admin / Staff)
- [ ] Academic year rollover tool
- [ ] Sibling discount support
