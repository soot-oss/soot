#ifndef NATIVEHOST_VERSION_COMPARE_H
#define NATIVEHOST_VERSION_COMPARE_H

#include <string>

/**
 * Orders version directory names such as "8.0.15" / "10.0.7" numerically per dot-separated component, so that a
 * lexicographic sort cannot put "10.0.7" before "9.0.1". Components without digits compare as text.
 */
inline int version_compare(const std::wstring& left, const std::wstring& right) {
    size_t i = 0;
    size_t j = 0;
    while (i < left.size() && j < right.size()) {
        const wchar_t a = left[i];
        const wchar_t b = right[j];
        if (a == L'.' && b == L'.') {
            ++i;
            ++j;
            continue;
        }
        if (a >= L'0' && a <= L'9' && b >= L'0' && b <= L'9') {
            size_t end_a = i;
            size_t end_b = j;
            while (end_a < left.size() && left[end_a] >= L'0' && left[end_a] <= L'9')
                ++end_a;
            while (end_b < right.size() && right[end_b] >= L'0' && right[end_b] <= L'9')
                ++end_b;
            // Skip leading zeros, then compare by length first: "9" is shorter than "10".
            size_t first_a = i;
            size_t first_b = j;
            while (first_a < end_a && left[first_a] == L'0')
                ++first_a;
            while (first_b < end_b && right[first_b] == L'0')
                ++first_b;
            const size_t len_a = end_a - first_a;
            const size_t len_b = end_b - first_b;
            if (len_a != len_b)
                return len_a < len_b ? -1 : 1;
            const int cmp = left.compare(first_a, len_a, right, first_b, len_b);
            if (cmp != 0)
                return cmp < 0 ? -1 : 1;
            i = end_a;
            j = end_b;
            continue;
        }
        if (a != b)
            return a < b ? -1 : 1;
        ++i;
        ++j;
    }
    if (i < left.size())
        return 1;
    if (j < right.size())
        return -1;
    return 0;
}

#endif
