#include <bits/stdc++.h>
#define NOPE 0

class KeyPath
{
private:
    struct E {
        int from;
        int l;
        int e;
        int to;
    };
    static bool KeyPath_cmp(E &a, E &b);
    bool topo(int n, int **matrix, int *topo, int *retopo);
    void Setve(int n, int **matrix, int *&ve, int *&toposort);
    void Setvl(int n, int **matrix, int *&vl, int *&ve, int *&retoposort);

public:
    std::string CalculateKeyPath_to_string(int n, int **matrix);
};

bool KeyPath::KeyPath_cmp(E &a, E &b)
{
    return a.e < b.e;
}
bool KeyPath::topo(int n, int **matrix, int *topo, int *retopo)
{
    int indegree[n];
    for (int i = 0; i < n; ++i) {
        indegree[i] = 0;
    }
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            if (matrix[i][j] != NOPE) {
                ++indegree[j];
            }
        }
    }

    std::stack<int> s;
    int vis[n];
    for (int i = 0; i < n; ++i) {
        vis[i] = 0;
        if (!indegree[i]) {
            s.push(i);
            vis[i] = 1;
        }
    }
    int pivot = 1;
    while (!s.empty()) {
        int head = s.top();
        topo[pivot] = head;
        retopo[n - pivot + 1] = head;
        ++pivot;
        s.pop();
        for (int i = 0; i < n; ++i) {
            if (matrix[head][i] != NOPE)
                --indegree[i];
            if (indegree[i] == 0 && !vis[i]) {
                s.push(i);
                vis[i] = 1;
            }
        }
    }
    if (pivot != (n + 1)) {
        return false;
    }
    return true;
}
void KeyPath::Setve(int n, int **matrix, int *&ve, int *&toposort)
{
    for (int i = 0; i < n; ++i) {
        ve[i] = 0;
    }
    for (int j = 1; j <= n; ++j) {
        int k = toposort[j];
        for (int i = 0; i < n; ++i) {
            if (matrix[k][i] != NOPE && ve[k] + matrix[k][i] > ve[i]) {
                ve[i] = ve[k] + matrix[k][i];
            }
        }
    }
}

void KeyPath::Setvl(int n, int **matrix, int *&vl, int *&ve, int *&retoposort)
{
    for (int i = 0; i < n; ++i) {
        vl[i] = ve[n - 1];
    }
    for (int j = 1; j <= n; ++j) {
        int k = retoposort[j];
        for (int i = 0; i < n; i++) {
            if (matrix[i][k] != NOPE && vl[k] - matrix[i][k] < vl[i]) {
                vl[i] = vl[k] - matrix[i][k];
            }
        }
    }
}

std::string KeyPath::CalculateKeyPath_to_string(int n, int **matrix)
{
    int *toposort = new int[n + 1];
    int *re_toposort = new int[n + 1];
    bool t = topo(n, matrix, toposort, re_toposort);
    if (!t) {
        return "no path";
    }
    int *ve = new int[n];
    int *vl = new int[n];
    Setve(n, matrix, ve, toposort);
    Setvl(n, matrix, vl, ve, re_toposort);
    E edge[(n + 1) * (n + 1)];
    int tot = 1;
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            if (matrix[i][j] != NOPE) {
                edge[tot].from = i;
                edge[tot].to = j;
                ++tot;
            }
        }
    }
    for (int i = 1; i < tot; ++i) {
        edge[i].e = ve[edge[i].from];
        edge[i].l = vl[edge[i].to] - matrix[edge[i].from][edge[i].to];
    }
    std::vector<E> tmp;
    for (int i = 1; i < tot; ++i) {
        if (edge[i].e == edge[i].l) {
            tmp.push_back(edge[i]);
        }
    }
    if (tmp.size() == 0) {
        return "no path";
    }
    sort(tmp.begin(), tmp.end(), KeyPath_cmp);
    int tttt = tmp[0].from;
    std::string res = std::to_string(tttt);
    std::string sy = " -> ";
    res = res + sy;
    res = res + std::to_string(tmp[0].to);
    for (int i = 1; i < (int)tmp.size(); ++i) {
        res = res + sy;
        res = res + std::to_string(tmp[i].to);
    }
    return res;
}
