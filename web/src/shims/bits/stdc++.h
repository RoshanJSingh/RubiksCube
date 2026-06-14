// Minimal <bits/stdc++.h> replacement for non-GCC toolchains (Emscripten/libc++).
// The project's headers include <bits/stdc++.h>, which only ships with libstdc++.
// We put this directory first on the include path for the WASM build so the same
// source compiles unchanged. It pulls in the standard headers the code uses
// (notably <fstream>, which the pattern-database loader needs).
#ifndef WEB_SHIM_BITS_STDCPP_H
#define WEB_SHIM_BITS_STDCPP_H

// C library
#include <cassert>
#include <cctype>
#include <cerrno>
#include <cfloat>
#include <climits>
#include <cmath>
#include <cstdarg>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <cwchar>
#include <cwctype>

// Containers
#include <array>
#include <bitset>
#include <deque>
#include <forward_list>
#include <list>
#include <map>
#include <queue>
#include <set>
#include <stack>
#include <unordered_map>
#include <unordered_set>
#include <vector>

// I/O
#include <fstream>
#include <iomanip>
#include <ios>
#include <iostream>
#include <istream>
#include <ostream>
#include <sstream>
#include <streambuf>

// General utilities
#include <algorithm>
#include <chrono>
#include <complex>
#include <exception>
#include <functional>
#include <initializer_list>
#include <iterator>
#include <limits>
#include <memory>
#include <new>
#include <numeric>
#include <random>
#include <ratio>
#include <stdexcept>
#include <string>
#include <tuple>
#include <typeinfo>
#include <utility>
#include <valarray>

#endif // WEB_SHIM_BITS_STDCPP_H
