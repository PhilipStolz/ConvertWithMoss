// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sampleconverter.file.wav;

import de.mossgrabers.sampleconverter.exception.ParseException;
import de.mossgrabers.sampleconverter.file.wav.SampleChunk.SampleChunkLoop;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Command line utility to look for specific settings in a sample chunk.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DumpSampleChunk
{
    private static final Logger LOGGER;
    static
    {
        System.setProperty ("java.util.logging.SimpleFormatter.format", "%5$s%n");
        LOGGER = Logger.getLogger ("de.mossgrabers.wav.DumpSampleChunk");
    }


    /**
     * The main function.
     *
     * @param args First parameter is the folder to scan
     */
    public static void main (final String [] args)
    {
        if (args == null || args.length == 0)
            LOGGER.info ("Give a folder as parameter...");
        else
            detect (new File (args[0]));
    }


    /**
     * Detect all wave files in the given folder and dump sample chunk data.
     *
     * @param folder The folder in which to search
     */
    private static void detect (final File folder)
    {
        Arrays.asList (folder.listFiles ()).forEach (file -> {
            if (file.isDirectory ())
            {
                detect (file);
                return;
            }

            if (file.getName ().toLowerCase (Locale.US).endsWith (".wav"))
            {
                try
                {
                    final WaveFile sampleFile = new WaveFile (file, true);

                    final InstrumentChunk instrumentChunk = sampleFile.getInstrumentChunk ();
                    if (instrumentChunk == null)
                        log ("No instrument chunk.", file);
                    else
                        log (instrumentChunk.infoText (), file);

                    final FormatChunk formatChunk = sampleFile.getFormatChunk ();
                    if (formatChunk == null)
                        log ("No format chunk.", file);
                    else
                        log (formatChunk.infoText (), file);

                    final SampleChunk sampleChunk = sampleFile.getSampleChunk ();
                    if (sampleChunk == null)
                    {
                        log ("Found Sample without SMPL chunk ", file);
                        return;
                    }

                    final int midiUnityNote = sampleChunk.getMIDIUnityNote ();
                    if (midiUnityNote != 0)
                        log ("Found MIDI unity note " + midiUnityNote, file);
                    final int midiPitchFraction = sampleChunk.getMIDIPitchFraction ();
                    if (midiPitchFraction != 0)
                        log ("Found MIDI pitch fraction " + midiPitchFraction, file);

                    final List<SampleChunkLoop> loops = sampleChunk.getLoops ();
                    final int loopSize = loops.size ();
                    if (loopSize > 1)
                        log ("Found " + loopSize + " loops", file);
                    if (!loops.isEmpty ())
                    {
                        final SampleChunkLoop loop = loops.get (0);
                        final int loopFraction = loop.getFraction ();
                        if (loopFraction != 0)
                            log ("Found loop with fraction " + loopFraction, file);
                        final int loopType = loop.getType ();
                        if (loopType > 0)
                            log ("Found loop type " + loopType, file);
                    }
                }
                catch (IOException | ParseException ex)
                {
                    log (ex.getMessage (), file);
                }
            }
        });
    }


    private static void log (final String message, final File file)
    {
        LOGGER.log (Level.INFO, "{0} in: {1}", new Object []
        {
            message,
            file.getAbsolutePath ()
        });
    }
}
