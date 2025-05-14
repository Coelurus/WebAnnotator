#include <stdio.h>
#include <windows.h>
#include "gestic_api.h"

#ifdef _WIN32
    #define EXPORT __declspec(dllexport)
#else
    #define EXPORT __attribute__((visibility("default")))
#endif

gestic_t gestic;
const gestic_position_t* pos;

EXPORT int gestic_setup() {
    const int stream_flags = gestic_data_mask_position | gestic_data_mask_sd;
    pos = &gestic.result.pos;

    gestic_initialize(&gestic);

    if (gestic_open(&gestic) < 0)
        return -1;

    gestic_set_auto_calibration(&gestic, 1, 100);
    gestic_select_frequencies(&gestic, gestic_all_freq, 100);
    gestic_set_approach_detection(&gestic, 0, 100);
    gestic_set_output_enable_mask(&gestic, stream_flags, stream_flags, gestic_data_mask_all, 100);

    return 0;
}

EXPORT int get_position(int* x, int* y, int* z) {
    while (!gestic_data_stream_update(&gestic, 0)) {
        // read data
    }

    *x = pos->x;
    *y = pos->y;
    *z = pos->z;

    return 0;
}

EXPORT void gestic_shutdown() {
    gestic_close(&gestic);
    gestic_cleanup(&gestic);
}

// LINUX: gcc -shared -fPIC -o libgestic.so stream-static.c - I./../../../api/include/ -L./../../../api/lib
// WIN cl /LD stream-static.c /I"..\..\..\api\include" ..\..\..\api\lib\gestic.lib /link /OUT:stream.dll

