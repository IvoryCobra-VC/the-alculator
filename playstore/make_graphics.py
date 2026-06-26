#!/usr/bin/env python3
"""Generate Play Store graphics for the Alculator in the limewash aesthetic."""
import os
import math
import random
from PIL import Image, ImageDraw, ImageFont, ImageFilter

OUT = os.path.dirname(os.path.abspath(__file__))

# — Limewash palette (matches the app's Theme.kt) —
CHALK      = (231, 224, 210)   # #E7E0D2 base
CHALK_PALE = (246, 241, 230)   # highlight
CHALK_DEEP = (206, 193, 169)   # shadow
ESPRESSO   = (58, 51, 42)      # #3A332A
TAUPE      = (140, 130, 112)   # #8C8270
CLAY       = (176, 101, 74)    # #B0654A
CLAY_DIM   = (143, 78, 55)     # #8F4E37

SERIF_BOLD = "/usr/share/fonts/truetype/dejavu/DejaVuSerif-Bold.ttf"
SANS       = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"


def mottled(size):
    """A chalk canvas with soft cloudy limewash blotches."""
    w, h = size
    img = Image.new("RGB", size, CHALK)
    blob = Image.new("L", size, 0)
    bd = ImageDraw.Draw(blob)
    rng = random.Random(7)
    # bright clouds
    light = Image.new("RGB", size, CHALK_PALE)
    for _ in range(14):
        cx, cy = rng.randint(0, w), rng.randint(0, h)
        r = rng.randint(int(w * 0.12), int(w * 0.30))
        bd.ellipse([cx - r, cy - r, cx + r, cy + r], fill=rng.randint(60, 130))
    blob = blob.filter(ImageFilter.GaussianBlur(w * 0.06))
    img = Image.composite(light, img, blob)
    # deep clouds
    blob2 = Image.new("L", size, 0)
    bd2 = ImageDraw.Draw(blob2)
    dark = Image.new("RGB", size, CHALK_DEEP)
    for _ in range(12):
        cx, cy = rng.randint(0, w), rng.randint(0, h)
        r = rng.randint(int(w * 0.10), int(w * 0.26))
        bd2.ellipse([cx - r, cy - r, cx + r, cy + r], fill=rng.randint(50, 110))
    blob2 = blob2.filter(ImageFilter.GaussianBlur(w * 0.06))
    img = Image.composite(dark, img, blob2)
    return img


def draw_glass(draw, cx, top, glass_h, top_w, bot_w, fill_frac, outline_w):
    """Tapered tumbler with a clay liquid fill, centered on cx."""
    bottom = top + glass_h
    tl = (cx - top_w / 2, top)
    tr = (cx + top_w / 2, top)
    br = (cx + bot_w / 2, bottom)
    bl = (cx - bot_w / 2, bottom)

    def lerp_x(side, y):
        t = (y - top) / glass_h
        if side == "l":
            return tl[0] + (bl[0] - tl[0]) * t
        return tr[0] + (br[0] - tr[0]) * t

    fill_top = bottom - glass_h * fill_frac
    fl = (lerp_x("l", fill_top), fill_top)
    fr = (lerp_x("r", fill_top), fill_top)
    draw.polygon([fl, fr, br, bl], fill=CLAY)
    draw.line([tl, tr], fill=CLAY_DIM, width=outline_w)
    draw.line([tr, br], fill=CLAY_DIM, width=outline_w)
    draw.line([br, bl], fill=CLAY_DIM, width=outline_w)
    draw.line([bl, tl], fill=CLAY_DIM, width=outline_w)
    # measure tick
    ty = top + glass_h * 0.28
    draw.line([(cx + top_w * 0.12, ty), (lerp_x("r", ty) - outline_w, ty)],
              fill=CLAY_DIM, width=max(2, outline_w - 2))


# ---------- 512x512 icon ----------
def make_icon():
    S = 512
    img = mottled((S, S))
    d = ImageDraw.Draw(img)
    draw_glass(d, cx=S / 2, top=150, glass_h=230, top_w=190, bot_w=150,
               fill_frac=0.55, outline_w=14)
    img.save(os.path.join(OUT, "icon-512.png"))
    print("icon-512.png", img.size)


# ---------- 1024x500 feature graphic ----------
def make_feature():
    W, H = 1024, 500
    img = mottled((W, H))
    d = ImageDraw.Draw(img)
    # glass on the right
    draw_glass(d, cx=830, top=120, glass_h=270, top_w=150, bot_w=120,
               fill_frac=0.55, outline_w=10)
    # wordmark
    title_font = ImageFont.truetype(SERIF_BOLD, 116)
    sub_font = ImageFont.truetype(SANS, 38)
    d.text((70, 150), "the", font=ImageFont.truetype(SERIF_BOLD, 64), fill=TAUPE)
    d.text((70, 205), "Alculator", font=title_font, fill=ESPRESSO)
    # clay rule
    d.rectangle([74, 345, 74 + 150, 351], fill=CLAY)
    d.text((74, 372), "Best value, by the unit", font=sub_font, fill=CLAY_DIM)
    img.save(os.path.join(OUT, "feature-1024x500.png"))
    print("feature-1024x500.png", img.size)


if __name__ == "__main__":
    make_icon()
    make_feature()
