#include <iostream>
#include <fstream>

bool FileToMatrix(std::string path, int ** &matrix, int &n)
{
    std::ifstream ifs(path);
    if (!ifs) {
        fprintf(stderr, "FILE_TO_MATRIX()::OPEN_FILE_ERROR\n");
        return false;
    }
    ifs >> n;
    matrix = new int *[n];
    for (int i = 0; i < n; ++i) {
        matrix[i] = new int[n];
        for (int j = 0; j < n; ++j) {
            ifs >> matrix[i][j];
        }
    }
    ifs.close();
    return true;
}

bool MatrixToFile(std::string path, int **matrix, int n)
{
    std::ofstream ofs(path);
    if (!ofs) {
        fprintf(stderr, "MATRIX_TO_FILE()::OPEN_FILE_ERROR\n");
        return false;
    }
    ofs << n << std::endl;
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            ofs << matrix[i][j] << " ";
        }
        ofs << std::endl;
    }
    ofs.close();
    return true;
}
