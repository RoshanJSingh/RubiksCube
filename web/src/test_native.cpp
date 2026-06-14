// Native verification harness for the WebCube wrapper. Built with g++ so we can
// confirm the existing solver works through our interface and measure real solve
// times (used to pick sensible scramble caps for the UI). Not part of the WASM build.

#include "cube_solver.hpp"
#include <chrono>
#include <random>

using namespace std;

static const char *FACE_TOKENS[6] = {"U", "D", "L", "R", "F", "B"};

// Build a scramble of `len` moves in standard notation, never turning the same
// face twice in a row (so the scramble doesn't trivially cancel).
static string makeScramble(int len, mt19937 &rng)
{
    static const char *suffix[3] = {"", "'", "2"};
    string s;
    int lastFace = -1;
    for (int i = 0; i < len; i++)
    {
        int face;
        do
        {
            face = rng() % 6;
        } while (face == lastFace);
        lastFace = face;
        int suf = rng() % 3;
        if (i)
            s += " ";
        s += string(FACE_TOKENS[face]) + suffix[suf];
    }
    return s;
}

int main()
{
    mt19937 rng(123456789u);

    cout << "Solved-state stickers: " << WebCube().state() << "\n\n";
    cout.flush();

    for (int L = 3; L <= 7; L++)
    {
        // Average over a few scrambles per length for a stable timing read.
        int trials = (L <= 6) ? 5 : 2;
        double totalTime = 0;
        bool allSolved = true;
        string lastScramble, lastSol;
        for (int t = 0; t < trials; t++)
        {
            string scramble = makeScramble(L, rng);

            WebCube wc;
            wc.applyMoves(scramble);

            auto t0 = chrono::high_resolution_clock::now();
            string sol = wc.solve(L);
            auto t1 = chrono::high_resolution_clock::now();
            totalTime += chrono::duration<double>(t1 - t0).count();

            // Verify: scramble then solution must return to solved.
            WebCube check;
            check.applyMoves(scramble);
            check.applyMoves(sol);
            if (!check.solved())
                allSolved = false;

            lastScramble = scramble;
            lastSol = sol;
        }
        cout << "L=" << L
             << "  avg_solve=" << (totalTime / trials) << "s"
             << "  verified=" << (allSolved ? "YES" : "NO")
             << "  e.g. [" << lastScramble << "] -> [" << lastSol << "]" << endl;
    }
    return 0;
}
