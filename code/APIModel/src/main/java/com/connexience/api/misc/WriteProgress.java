package com.connexience.api.misc;

import java.io.PrintStream;


/**
 * <p>A very basic implementation of the {@link IProgressInfo} interface which writes to 
 * a {@link java.io.PrintStream} (or <code>java.lang.System.out</code> if no <code>PrintStream</code> is provided) 
 * a given text message followed by the percent of progress reported.</p>
 * <p>
 * An sample output could be:
 * 
 * <pre>some text message: 99%</pre>
 * 
 * or
 * 
 * <pre>99%</pre>
 * 
 * if no message was provided.
 * </p>
 * <p>
 * Note that the class uses special character <code>\r</code> to print to the <code>PrintStream</code>, so
 * it may no fit streams that cannot do actual caret return. It also means that to work correctly, the text message should not
 * be longer than a single line in the output..
 * </p>
 * 
 * <h2>Direct known subclasses:</h2>
 * {@link com.connexience.shell-tools.misc.AdaptiveWriteProgress}
 * 
 * @author njc97
 *
 */
public class WriteProgress implements IProgressInfo
{
    protected long _totalLength;
    protected String _message;
    protected String _lastOutput;
    protected PrintStream _outStream;
    
    public WriteProgress()
    {
        this(System.out, null);
    }

    public WriteProgress(String message)
    {
        this(System.out, message);
    }
    
    public WriteProgress(PrintStream outStream)
    {
        this(outStream, null);
    }
    
    public WriteProgress(PrintStream outStream, String message)
    {
        _outStream = outStream;
        _message = message;
        _lastOutput = "";
    }

    
    public void setMessage(String message)
    {
        _message = message;
    }
    
    public void setOutStream(PrintStream outStream) {
        _outStream = outStream;
    }

    @Override
    public void reportBegin(long totalLength)
    {
        _totalLength = totalLength;
        reportProgress(0);
    }

    @Override
    public void reportProgress(long currentLength)
    {
        StringBuilder sb = new StringBuilder("\r");
        if (_message != null) {
            sb.append(_message);
            sb.append(": ");
        }
        sb.append(getPercentString(currentLength, _totalLength));
        sb.append("%");

        // As we us '\r' to move to the beginning of the line, we need to ensure that 
        // the line is properly cleared in the case the current output is shorter 
        // than the last one.
        // This may happen e.g. because message has changed to a shorter one or 
        // the percent string is shorter (shows less decimal places). 
        while (sb.length() < _lastOutput.length()) {
            sb.append(' ');
        }

        String output = sb.toString();
        _outStream.print(output);
        _outStream.flush();

        _lastOutput = output;
    }
    
    @Override
    public void reportEnd(long currentLength)
    {
        reportProgress(currentLength);
        _outStream.println();
    }


    protected String getPercentString(long currentLength, long totalLength)
    {
        return Long.toString((long)((double)currentLength / _totalLength * 100 + 0.5));
    }
}
