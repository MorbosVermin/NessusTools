using nessus_tools;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace NessusMerge
{
    public partial class Form1 : Form
    {

        public bool Wait
        {
            get { return Application.UseWaitCursor; }
            set
            {
                Application.UseWaitCursor = value;
                if (value)
                {
                    Application.DoEvents();
                    progressBar.Style = ProgressBarStyle.Marquee;
                }
                else
                {
                    progressBar.Style = ProgressBarStyle.Continuous;
                    progressBar.Value = 0;
                }
            }
        }

        private class Report
        {
            private NessusClientData_v2 scanData;
            public Image Icon { get { return Properties.Resources.Nessus; } }
            public string PolicyName { get { return scanData.Policy.Name; } }
            public string ReportName { get { return scanData.Report.Name; } }
            public int HostCount { get { return scanData.Report.ReportHosts.Count; } }

            public Report(NessusClientData_v2 data)
            {
                scanData = data;
            }

            public NessusClientData_v2 GetReport()
            {
                return scanData;
            }
        }

        public string Filter { get { return "Nessus Scan Results (*.nessus)|*.nessus|Nessus Scan Results (*.xml)|*.xml|All files (*.*)|*.*"; } }

        private BindingList<Report> reports;

        public Form1()
        {
            InitializeComponent();
            this.reports = new BindingList<Report>();
            backgroundWorker1.RunWorkerCompleted += backgroundWorker1_RunWorkerCompleted;
        }

        void backgroundWorker1_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            Wait = false;
            NessusClientData_v2 report = (NessusClientData_v2)e.Result;
            saveFileDialog1.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            saveFileDialog1.FileName = "";
            saveFileDialog1.Filter = Filter;
            DialogResult r = saveFileDialog1.ShowDialog(this);
            if (r == System.Windows.Forms.DialogResult.OK)
            {
                NessusClientData_v2.Save(report, saveFileDialog1.FileName);
                statusLabel.Text = String.Format("Successfully merged {0} report(s) at {1}.", report.Report.ReportHosts.Count, saveFileDialog1.FileName);
            }
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            //Add report
            openFileDialog1.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            openFileDialog1.Filter = Filter;
            openFileDialog1.FileName = "";
            DialogResult r  = openFileDialog1.ShowDialog(this);
            if (r == System.Windows.Forms.DialogResult.OK)
            {
                NessusClientData_v2 data = NessusClientData_v2.Parse(openFileDialog1.FileName);
                reports.Add(new Report(data));
            }
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            //Remove report
            foreach (Report report in dataGridView1.SelectedRows)
            {
                reports.Remove(report);
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            dataGridView1.DataSource = reports;
            dataGridView1.Columns[0].Width = 32;
            dataGridView1.Columns[1].Width = 200;
            dataGridView1.Columns[2].Width = 200;
            dataGridView1.Columns[3].AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
        }

        private void toolStripButton4_Click(object sender, EventArgs e)
        {
            //Clear all reports.
            reports.Clear();
        }

        private void toolStripButton3_Click(object sender, EventArgs e)
        {
            if (reports.Count == 0)
                return;

            Wait = true;
            statusLabel.Text = "Please wait, merging report(s)...";
            backgroundWorker1.RunWorkerAsync();
        }

        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            //Merge reports
            NessusClientData_v2 report = reports[0].GetReport();
            for (int i = 1; i < reports.Count; i++)
                foreach (ReportHost host in reports[i].GetReport().Report.ReportHosts)
                    report.Report.ReportHosts.Add(host);

            e.Result = report;
        }
    }
}
