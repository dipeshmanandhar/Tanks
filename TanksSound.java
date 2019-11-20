import java.io.File;
import javax.sound.midi.Sequencer;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Instrument;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiSystem;
public class TanksSound
{
   private static MidiChannel[] channels;
   private static Instrument[] instruments;
   //private static Sequencer sequencer;
   private static boolean playing;
   private static int distance=1000;
                              // C, D,D#, F, G, A,A#, C
                              // 0, 2, 3, 5, 7, 9,10,12
                              // C, D,D#,F#, G, A,A#, C
                              // 0, 2, 3, 6, 7, 9,10,12
                              // C, D, E, G, A, C
                              // 0, 2, 4, 7, 9,12
   private static int[] dorian={0,2,3,5,7,9,11,12},
                        dorian4S={0,2,3,6,7,9,11,12},
                        majorPentatonic={0,2,4,7,9,12};
   private static int[] backgroundSong;
   //instruments (0~127) (also the range for pitch and volume)
   protected static final int PIANO=0,
                              XYLOPHONE=13,
                              SYNTH_DRUM=118,
                              GUNSHOT=127;
   public static void initialize()
   {
      try 
      {
         Synthesizer synth = MidiSystem.getSynthesizer();
         synth.open();
         
         /*
         sequencer = MidiSystem.getSequencer();
         sequencer.open();
         sequencer.setSequence(MidiSystem.getSequence(new File("songs/NowOrNeverQuiet.mid")));
         sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
         */
         
         //sequencer.getTransmitter().setReceiver(synth.getReceiver());
         
         channels = synth.getChannels();
         instruments = synth.getDefaultSoundbank().getInstruments();
         
         play();
      }
      catch (Exception ignored) 
      {
         
      }
      
      /*
      channels[0].programChange(instr[PIANO].getPatch().getProgram());
      channels[1].programChange(instr[PIANO].getPatch().getProgram());
      
      channels[0].allNotesOff();
      channels[1].allNotesOff();
      */
      backgroundMusic();
   }
   public static void pickSong()
   {
      double rand=Math.random();
      if(rand<1/3.0)
         backgroundSong=dorian;
      else if(rand<2/3.0)
         backgroundSong=dorian4S;
      else
         backgroundSong=majorPentatonic;
         
   }
   public static void silence()
   {
      channels[0].allNotesOff();
      //sequencer.stop();
      playing=false;
      
   }
   public static void play()
   {
      //sequencer.start();
      playing=true;
   }
   public static void setDistance(double d)
   {
      distance=(int)d;
   }
   public static void backgroundMusic()
   {
      Thread music=
         new Thread()
         {
            @Override
            public void run()
            {
               long prevNoteTime=0,noteLength=distance;
               
               while(true)
               {
                  noteLength=distance;
                  if(playing)
                  {
                     long now=System.currentTimeMillis();
                     if(now-noteLength>prevNoteTime)
                     {
                        channels[0].programChange(instruments[PIANO].getPatch().getProgram());
                        int pitch = 48+backgroundSong[(int)(Math.random()*backgroundSong.length)];
                        int volume = (int)(Math.random()*11)+30;
                        channels[0].noteOn(pitch, volume);
                     
                        prevNoteTime=now;
                     }
                  }
                  Thread.yield();
               }
            }
         };
      music.start();
   }
   public static void explosion()
   {
      channels[0].programChange(instruments[GUNSHOT].getPatch().getProgram());
      int pitch = (int)(Math.random()*11)+30;
      int volume = (int)(Math.random()*8)+120;
      channels[0].noteOn(pitch, volume);
   }
   public static void shot()
   {
      channels[0].programChange(instruments[GUNSHOT].getPatch().getProgram());
      int pitch = (int)(Math.random()*11)+50;
      int volume = (int)(Math.random()*11)+40;
      channels[0].noteOn(pitch, volume);
   }
   public static void selection()
   {
      channels[0].programChange(instruments[SYNTH_DRUM].getPatch().getProgram());
      int pitch = (int)(Math.random()*11)+50;
      int volume = (int)(Math.random()*8)+120;
      channels[0].noteOn(pitch, volume);
   }
   public static void damage()
   {
      channels[0].programChange(instruments[XYLOPHONE].getPatch().getProgram());
      int pitch = (int)(Math.random()*11)+15;
      int volume = (int)(Math.random()*8)+120;
      channels[0].noteOn(pitch, volume);
   }
}