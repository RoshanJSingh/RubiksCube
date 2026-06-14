// Minimal <bits/stdc++.h> replacement for non-GCC toolchains (Emscripten/libc++).
// The project's headers include <bits/stdc++.h>, which only ships with libstdc++.
// We put this directory first on the include path for the WASM build so the same
// source compiles unchanged. It just pulls in the standard headers the code uses.
#ifndef WEB_SHIM_BITS_STDCPP_H
#define WEB_SHIM_BITS_STDCPP_H

#include <algorithm>
#include <array>
#include <bitset>
#include <cassert>
#include <cctype>
#include <cfloat>
#include <climits>
#include <cmath>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <deque>
#include <functional>
#include <iostream>
#include <iterator>
#include <limits>
#include <list>
#include <map>
#include <memory>
#include <numeric>
#include <queue>
#include <set>
#include <sstream>
#include <stack>
#include <stdexcept>
#include <string>
#include <tuple>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

#endif // WEB_SHIM_BITS_STDCPP_H
