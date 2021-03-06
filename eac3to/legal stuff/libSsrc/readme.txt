
libSsrc.dll is a little win32 sample rate converter library
created 2008-10-19 by madshi.net

this lib is based on Otachan's foobar2000 plugin
which is based on Naoki Shibata original SSRC sampling rate converter
below you'll find the original description by Naoki Shibata


-------------------------------------------------------------------------------


  A fast and high quality sampling rate converter SSRC
                                           written by Naoki Shibata


Homepage : http://shibatch.sourceforge.net/
e-mail   : shibatch@users.sourceforge.net


<Features>
  This program converts sampling rate of PCM wav file. Also, this program has
a function to apply dither to its output and extend perceived dynamic range.
  Sampling rates of 44.1kHz and 48kHz are populary used, but ratio of these
two frequency is 147:160, and it's not a small numbers. Therefore, sampling
rate conversion without degradation of sound quality requires filter with very
large order, and it's difficult to achive both quality and speed. This program
achived relatively fast and high quality with two different kinds of filters
combined skillfully.


<Usage>

ssrc [<options>] <input wav file> <output wav file>

Usage of options are as follows :
  --rate <sampling rate>
    Specify sampling rate of output file.
  --att <value(dB)>
    Attenuate volume of output by specified value.
  --twopass
    Perform two pass processing so that clipping is avoided.
    At the first pass, the program converts sampling rate of input file and
    write to a temporary file in float numbers while scanning clippings. At
    the second pass, the program attenuate the volume so that clipping is
    prevented, and write to the output file.
  --normalize
    Normalize the wave file.
  --dither [<type>]
    Apply dithers to the output file.
      type 0 : no dither
      type 1 : no noise shaping
      type 2 : triangular dither
      type 3 : ATH based noise shaping
  --bits
    Specify quantization bit length. 8, 16 and 24bits are supported.
  --quiet
    Nothing is displayed except error.
  --pdf <type> [<amp>]
    Select probability distribution function and amplitude of noise.
      type 0 : rectangular
      type 1 : triangular
      type 2 : Gaussian
  --profile
    Specify profile
      "standard" profile : the default setting
      "fast"     profile : about x2 speed, not so bad quality

  Only PCM coded wav files are used as input and output files.
  Input and output sampling frequency must satisfy a certain condition, but
(probably) conversions between all populary used sampling frequencies are
supported.
  If sampling frequencies of input and output are same, sampling rate
conversion is not performed and only conversion of quantization bit length
with optional dithering are done.


<Compiling method>
  Specify endianness in the Makefile and just type make.


<Caveats>
  Cumulatively applying dithers degrades sound quality. If you are editing
sound files, save master file as non-dithered 24bit file, and apply dither
only to the final 16bit output file.
  Dithered 8bit files contains strong supersonic, and listening to these
files for long hours may damage your hearing. Dithered 16bit files are no
problem since the power of the supersonic of dither is 1/65536 of those of
dithered 8bit files.


<Conditions of distribution>
  This program(except FFT and Bessel function part) is distributed under
LGPL. See LGPL.txt for details. But, if you make a new program with derived
code from this program,I strongly wish that my name and derived code are
indicated explicitly.

  FFT and Bessel function part is a routine made by Mr.Ooura. This routine
is a freeware. Contact Mr.Ooura for details of distributing licenses.

http://momonga.t.u-tokyo.ac.jp/~ooura/fft.html
http://momonga.t.u-tokyo.ac.jp/~ooura/bessel.html
