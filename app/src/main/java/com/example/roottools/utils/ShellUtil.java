package com.example.roottools.utils;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.internal.MainShell;

import java.util.List;


public class ShellUtil {

    @SuppressLint("RestrictedApi")
    public static Shell.Job cmd(List<String> listCmd) {
        return MainShell.newJob(false, listCmd.toArray(new String[listCmd.size()]));
    }

    @SuppressLint("RestrictedApi")
    public static Shell.Job cmd(@NonNull String... commands) {
        return MainShell.newJob(false, commands);
    }
}
