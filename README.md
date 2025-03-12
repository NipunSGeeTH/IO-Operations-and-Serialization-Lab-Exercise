<h1>🛒 SuperSaver POS System</h1>

<p>
  A Java-based <strong>Point of Sale (POS) System</strong> developed as a group project by <strong>🚀 BitBuilders</strong> team.  
</p>

<h2>👥 Team Members</h2>
<ul>
  <li>BitBuilders - Group Collaboration</li>
</ul>

<h2>📦 Features</h2>
<ul>
  <li>CSV-based Item Database</li>
  <li>Billing System with Discounts</li>
  <li>Pending Bill Handling</li>
  <li>Total Revenue Reporting</li>
  <li>Email Revenue Report to Sales Team 📧</li>
  <li>Export Bills as PDF (basic or enhanced using PDFBox)</li>
</ul>

<h2>📧 Email Sending Feature</h2>
<p>
  ✔️ The project includes a feature to <strong>send the revenue report via email using SMTP</strong>.<br>
  ⚠️ <strong>To make it work:</strong> Please remember to <strong>change the email address and App Password</strong> in the code before testing.<br><br>
  ✏️ <strong>Edit these lines in the code:</strong><br>
</p>

<pre>
final String username = "your_email@gmail.com";
final String password = "your_app_password";
</pre>

<h2>📂 Required Libraries</h2>
<p>
  To run the email sending part, you need to download and include the following dependencies:
</p>
<ul>
  <li>📁 <code>javax.mail-1.6.2.jar</code></li>
  <li>📁 <code>activation-1.1.1.jar</code></li>
</ul>

<p>
  You can download them from:
</p>
<ul>
  <li><a href="https://mvnrepository.com/artifact/com.sun.mail/javax.mail/1.6.2" target="_blank">javax.mail-1.6.2.jar 🔗</a></li>
  <li><a href="https://mvnrepository.com/artifact/javax.activation/activation/1.1.1" target="_blank">activation-1.1.1.jar 🔗</a></li>
</ul>

<h2>▶️ Running the Project</h2>
<ol>
  <li>📌 Make sure you have Java installed.</li>
  <li>📌 Place your item CSV file in the same folder.</li>
  <li>📌 Compile and run the code using your terminal or VS Code.</li>
</ol>

<h2>✅ Sample Output</h2>
<ul>
  <li>Text Bill Output (bill.txt)</li>
  <li>Revenue Report (revenue.txt)</li>
  <li>Email sent with attachment 📎</li>
</ul>

<h2>💬 Notes</h2>
<ul>
  <li>This is an academic-level implementation for learning purposes.</li>
  <li>All classes were kept in a single file to comply with lab submission guidelines.</li>
</ul>

<h2>🔗 License</h2>
<p>This project is open for educational use.</p>
