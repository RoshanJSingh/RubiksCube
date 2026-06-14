// Native verification for the IDA* path using the real corner pattern database.
// Confirms the DB loads through the project's fromFile() and that IDA* returns
// verified solutions, and measures timing to pick a sensible scramble cap.
//
//   g++ -std=c++14 -O2 -o test_ida web/src/test_ida.cpp
//   ./test_ida "C:/Users/rosha/Downloads/cornerDepth5V1.txt"

#include "cube_solver.hpp"
#include <chrono>
#include <random>

using namespace std;

static const char *FACE_TOKENS[6] = {"U", "D", "L", "R", "F", "B"};

static string makeScramble(int len, mt19937 &rng)
{
    static const char *suffix[3] = {"", "'", "2"};
    string s;
    int lastFace = -1;
    for (int i = 0; i < len; i++)
    {
        int face;
        do { face = rng() % 6; } while (face == lastFace);
        lastFace = face;
        if (i) s += " ";
        s += string(FACE_TOKENS[face]) + suffix[rng() % 3];
    }
    return s;
}

int main(int argc, char **argv)
{
    string dbPath = (argc > 1) ? argv[1] : "C:/Users/rosha/Downloads/cornerDepth5V1.txt";
    cout << "DB path: " << dbPath << "\n";

    mt19937 rng(2024u);

    for (int L : {6, 8, 10, 12, 14, 16, 18, 20})
    {
        int trials = (L <= 12) ? 3 : 2;
        double total = 0;
        bool allSolved = true;
        int maxLen = 0;
        string lastScramble, lastSol;
        for (int t = 0; t < trials; t++)
        {
            string scramble = makeScramble(L, rng);

            WebCube wc;
            wc.setDbPath(dbPath);
            wc.applyMoves(scramble);

            auto t0 = chrono::high_resolution_clock::now();
            string sol;
            try { sol = wc.solve("ida", 0); }
            catch (const char *e) { cout << "  THROW: " << e << "\n"; return 1; }
            catch (...) { cout << "  THROW: unknown\n"; return 1; }
            auto t1 = chrono::high_resolution_clock::now();
            total += chrono::duration<double>(t1 - t0).count();

            WebCube check;
            check.applyMoves(scramble);
            check.applyMoves(sol);
            if (!check.solved()) allSolved = false;
            int len = sol.empty() ? 0 : (int)splitTokens(sol).size();
            maxLen = max(maxLen, len);

            lastScramble = scramble;
            lastSol = sol;
        }
        cout << "L=" << L
             << "  avg_time=" << (total / trials) << "s"
             << "  verified=" << (allSolved ? "YES" : "NO")
             << "  solLen<=" << maxLen
             << "  e.g.[" << lastScramble << "] -> [" << lastSol << "]" << endl;
    }
    return 0;
}
