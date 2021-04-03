// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sampleconverter.file.wav;

import de.mossgrabers.sampleconverter.exception.CompressionNotSupportedException;
import de.mossgrabers.sampleconverter.exception.ParseException;
import de.mossgrabers.sampleconverter.file.riff.RIFFChunk;
import de.mossgrabers.sampleconverter.file.riff.RiffID;


/**
 * Accessor for a data chunk ("data") in a WAV file.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DataChunk extends WavChunk
{
    /**
     * Constructor.
     *
     * @param chunk The RIFF chunk which contains the data
     * @throws ParseException Length of data does not match the expected chunk size
     */
    public DataChunk (final RIFFChunk chunk) throws ParseException
    {
        super (RiffID.DATA_ID, chunk, chunk.getData ().length);
    }


    /**
     * Calculates the length of the data in samples.
     *
     * @param chunk The format chunk, necessary for the calculation (sample size and number of
     *            channels
     * @return The length of the audio file in samples
     * @throws CompressionNotSupportedException If the compression/encoding used for the data is not
     *             supported
     */
    public int calculateLength (final FormatChunk chunk) throws CompressionNotSupportedException
    {
        final int compressionCode = chunk.getCompressionCode ();
        if (compressionCode == FormatChunk.WAVE_FORMAT_PCM || compressionCode == FormatChunk.WAVE_FORMAT_IEEE_FLOAT)
            return this.chunk.getData ().length / (chunk.getNumberOfChannels () * chunk.getSignicantBitsPerSample () / 8);
        throw new CompressionNotSupportedException ("Unsupported data compression: " + FormatChunk.getCompression (compressionCode));
    }


    /**
     * Sets the data.
     *
     * Note: The array will not be cloned for performance reasons.
     *
     * @param data The data to set
     */
    public void setData (final byte [] data)
    {
        this.chunk.setData (data);
    }
}
