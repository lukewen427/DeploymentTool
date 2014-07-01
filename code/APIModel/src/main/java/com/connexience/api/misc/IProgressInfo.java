package com.connexience.api.misc;

public interface IProgressInfo
{
    void reportBegin(long totalLength);
    void reportProgress(long currentLength);
    void reportEnd(long currentLength);
}
