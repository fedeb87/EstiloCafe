package com.federicoberon.estilocafe;

import android.os.Environment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.federicoberon.estilocafe.utils.StorageUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;

public class StorageUtilTest {
    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();

    private File nonExistentDirectory;
    private File existentDirectory;

    @Before
    public void setup() {
        nonExistentDirectory = Mockito.mock(File.class);
        Mockito.when(nonExistentDirectory.exists()).thenReturn(false);

        existentDirectory = storageDirectory.getRoot();
    }

    @Test
    public void willUseTheExternalStoragePublicDirectoryWhenItIsAvailable() {
        // external storage is writeable
        Mockito.when(Environment.getExternalStorageDirectory()).thenReturn(existentDirectory);
        // the external storage public directory is available
        Mockito.when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)).thenReturn(existentDirectory);

        String logFilePath = StorageUtil.getMediaAbsolutePath();

        assertEquals(existentDirectory.getAbsolutePath() , logFilePath);
    }
}