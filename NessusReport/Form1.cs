using nessus_reporting;
using nessus_tools;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace NessusReport
{
    public partial class Form1 : Form
    {
        private class ReportTypeSelection
        {
            public NessusReports.ReportType Type { get; set; }

            public ReportTypeSelection(NessusReports.ReportType type)
            {
                Type = type;
            }

            public override string ToString()
            {
                switch (this.Type)
                {
                    case NessusReports.ReportType.HostsOnly:
                        return Properties.Resources.HostsOnly;

                    case NessusReports.ReportType.VulnsByFamily:
                        return Properties.Resources.VulnsByFamily;

                    default:
                        return Properties.Resources.VulnsByHost;
                
                }
            }
        }

        private bool Wait
        {
            get { return Application.UseWaitCursor; }
            set
            {
                Application.UseWaitCursor = value;
                if (value)
                {
                    Application.DoEvents();
                }

                button1.Enabled = (!value);
                button2.Enabled = (!value);
                comboBox1.Enabled = (!value);
            }
        }

        private string OpenFilter { get { return "Nessus Scan (*.nessus)|*.nessus|Nessus Scan (*.xml)|*.xml|All files (*.*)|*.*"; } }

        private string SaveFilter { get { return "Hyper-Text Markup Language (HTML)|*.html|Comma Separated Values (CSV)|*.csv"; } }

        private BindingList<ReportTypeSelection> reportTypeSelections;

        public Form1()
        {
            InitializeComponent();
            this.reportTypeSelections = new BindingList<ReportTypeSelection>();
            comboBox1.DataSource = this.reportTypeSelections;
            backgroundWorker1.RunWorkerCompleted += backgroundWorker1_RunWorkerCompleted;
        }

        void backgroundWorker1_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            Wait = false;
            bool ok = (bool)e.Result;
            if (!ok)
            {
                MessageBox.Show("Failed to export report.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            foreach (NessusReports.ReportType reportType in Enum.GetValues(typeof(NessusReports.ReportType)))
            {
                this.reportTypeSelections.Add(new ReportTypeSelection(reportType));
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            //generate
            saveFileDialog1.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            saveFileDialog1.FileName = "";
            saveFileDialog1.Filter = SaveFilter;
            DialogResult r = saveFileDialog1.ShowDialog(this);
            if (r == System.Windows.Forms.DialogResult.OK)
            {
                Wait = true;
                backgroundWorker1.RunWorkerAsync(saveFileDialog1.FileName);
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            //open nessus scan file
            openFileDialog1.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            openFileDialog1.FileName = "";
            openFileDialog1.Filter = OpenFilter;
            DialogResult r = openFileDialog1.ShowDialog(this);
            if (r == System.Windows.Forms.DialogResult.OK)
            {
                textBox1.Text = openFileDialog1.FileName;
            }
        }

        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            string inputPath = textBox1.Text;
            NessusReports.ReportType reportType = ((ReportTypeSelection)comboBox1.SelectedItem).Type;
            string outputPath = (string)e.Argument;
            string ext = Path.GetExtension(outputPath);
            NessusClientData_v2 data = NessusClientData_v2.Parse(inputPath);
            if (ext.Equals("html", StringComparison.CurrentCultureIgnoreCase))
            {
                HtmlNessusReport report = new HtmlNessusReport();
                report.Path = outputPath;
                e.Result = report.Export(data, reportType);
            }
            else
            {
                CsvNessusReport report = new CsvNessusReport();
                report.Path = outputPath;
                e.Result = report.Export(data, reportType);
            }
        }
    }
}
