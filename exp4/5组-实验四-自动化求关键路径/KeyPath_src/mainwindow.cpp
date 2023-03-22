#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "FileTools.h"
#include "QMessageBox"
#include "KeyPath.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::on_CalBtn_clicked()
{
    KeyPath K;
    int **matrix;
    int n = ui->spinBox->text().toInt();
    matrix = new int *[n];
    for (int i = 0; i < n; ++i)
        matrix[i] = new int[n];

    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            matrix[i][j] = ui->tableWidget->item(i, j)->text().toInt();
        }
    }

    ui->textBrowser->setText(QString::fromStdString(K.CalculateKeyPath_to_string(n, matrix)));
}

void MainWindow::on_spinBox_valueChanged(int arg1)
{
    ui->tableWidget->setRowCount(arg1);
    ui->tableWidget->setColumnCount(arg1);
}

void MainWindow::on_SaveBtn_clicked()
{
    int n = ui->spinBox->text().toInt();
    int **matrix = new int *[n];
    for (int i = 0; i < n; ++i)
        matrix[i] = new int [n];

    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            if (ui->tableWidget->item(i, j) == NULL) matrix[i][j] = 0;
            else matrix[i][j] = ui->tableWidget->item(i, j)->text().toInt();
        }
    }

    std::string Path = "D:/1.txt";
    if (MatrixToFile(Path, matrix, n)) {
        QMessageBox MyBox(QMessageBox::Information, "保存成功", "图邻接矩阵已保存于" + QString::fromStdString(Path), QMessageBox::Yes | QMessageBox::No);
        MyBox.exec();
    } else {
        QMessageBox MyBox(QMessageBox::Critical, "保存失败", "无法打开目标文件" + QString::fromStdString(Path), QMessageBox::Yes | QMessageBox::No);
        MyBox.exec();
    }
}

void MainWindow::on_LoadBtn_clicked()
{
    int n;
    int **matrix;
    std::string Path = "D:/1.txt";
    if (FileToMatrix(Path, matrix, n)) {
        ui->spinBox->setValue(n);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                ui->tableWidget->setItem(i, j, new QTableWidgetItem(QString::number(matrix[i][j])));
            }
        }
        QMessageBox MyBox(QMessageBox::Information, "加载成功", "成功从" + QString::fromStdString(Path) + "导入邻接矩阵", QMessageBox::Yes | QMessageBox::No);
        MyBox.exec();
    } else {
        QMessageBox MyBox(QMessageBox::Critical, "加载失败", "无法打开目标文件" + QString::fromStdString(Path), QMessageBox::Yes | QMessageBox::No);
        MyBox.exec();
    }
}
