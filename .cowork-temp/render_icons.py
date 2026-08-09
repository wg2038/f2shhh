#!/usr/bin/env python3
"""Render icon concepts as PNG previews using Pillow."""
from PIL import Image, ImageDraw, ImageFont
import math, os

OUTPUT_DIR = "/Users/cicada/Desktop/F2shhh/.cowork-temp"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# ── Concept B: Phone Back + DND Ring ─────────────────────────────────────
def render_concept_b():
    size = 512
    img = Image.new('RGBA', (size, size), (15, 15, 16, 255))
    d = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2

    # DND ring (cyan arc, ~270°)
    bbox = [cx-120, cy-120, cx+120, cy+120]
    d.arc(bbox, start=-80, end=200, fill=(128, 216, 255, 255), width=18)

    # Phone body (tilted -15°)
    phone_img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    pd = ImageDraw.Draw(phone_img)
    # Phone outline
    px1, py1, px2, py2 = cx-30, cy-50, cx+30, cy+50
    pd.rounded_rectangle([px1, py1, px2, py2], radius=8, fill=(226, 226, 230, 255))
    # Camera module
    pd.rounded_rectangle([px1+8, py1+8, px1+28, py1+28], radius=4, fill=(39, 41, 46, 255))
    # Lenses
    pd.ellipse([px1+11, py1+11, px1+17, py1+17], fill=(15, 15, 16, 255))
    pd.ellipse([px1+20, py1+11, px1+26, py1+17], fill=(15, 15, 16, 255))
    # Flash
    pd.ellipse([px1+11, py1+21, px1+16, py1+26], fill=(128, 216, 255, 180))

    phone_img = phone_img.rotate(-15, center=(cx, cy), resample=Image.BICUBIC)
    img.paste(phone_img, (0, 0), phone_img)

    # Silence bar
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([cx-18, cy+70, cx+18, cy+78], radius=3, fill=(128, 216, 255, 200))

    img.save(os.path.join(OUTPUT_DIR, "icon_concept_b.png"))
    print("Saved concept B")


# ── Concept C: Flip Cards + DND ──────────────────────────────────────────
def render_concept_c():
    size = 512
    img = Image.new('RGBA', (size, size), (15, 15, 16, 255))
    d = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2

    # Bottom card (dark, offset down-right)
    d.rounded_rectangle([cx-60+12, cy-60+12, cx+60+12, cy+60+12],
                        radius=14, fill=(26, 27, 31, 255), outline=(128, 216, 255, 100), width=6)

    # Top card (bright, offset up-left)
    d.rounded_rectangle([cx-60-12, cy-60-12, cx+60-12, cy+60-12],
                        radius=14, fill=(226, 226, 230, 255))

    # DND circle (ring)
    d.ellipse([cx-42, cy-42, cx+42, cy+42], outline=(128, 216, 255, 255), width=12)

    # DND horizontal bar
    d.rounded_rectangle([cx-24, cy-6, cx+24, cy+6], radius=3, fill=(128, 216, 255, 255))

    img.save(os.path.join(OUTPUT_DIR, "icon_concept_c.png"))
    print("Saved concept C")


# ── Concept E: Sound Wave → Phone → × ────────────────────────────────────
def render_concept_e():
    size = 512
    img = Image.new('RGBA', (size, size), (15, 15, 16, 255))
    d = ImageDraw.Draw(img)
    cy = size // 2

    # Sound waves (left)
    for i, (r, alpha, w) in enumerate([(80, 255, 12), (100, 180, 10), (120, 100, 8)]):
        bbox = [100 - r//2, cy - r//2, 100 + r//2, cy + r//2]
        d.arc(bbox, start=-50, end=50, fill=(128, 216, 255, alpha), width=w)

    # Phone silhouette (center)
    px1, py1, px2, py2 = 235, 150, 275, 362
    d.rounded_rectangle([px1, py1, px2, py2], radius=8, fill=(226, 226, 230, 255))
    # Screen tint
    d.rounded_rectangle([px1+4, py1+8, px2-4, py2-8], radius=4, fill=(128, 216, 255, 30))

    # × (right side)
    x1, y1 = 340, 200
    x2, y2 = 400, 260
    d.line([(x1, y1), (x2, y2)], fill=(128, 216, 255, 255), width=14)
    d.line([(x2, y1), (x1, y2)], fill=(128, 216, 255, 255), width=14)

    # Scatter dots
    d.ellipse([315, 280, 325, 290], fill=(128, 216, 255, 60))
    d.ellipse([410, 290, 418, 298], fill=(128, 216, 255, 40))

    img.save(os.path.join(OUTPUT_DIR, "icon_concept_e.png"))
    print("Saved concept E")


# ── Concept D2: S + dots ─────────────────────────────────────────────────
def render_concept_d2():
    size = 512
    img = Image.new('RGBA', (size, size), (15, 15, 16, 255))
    d = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2

    # Geometric S using arcs + lines
    # Top arc
    bbox_top = [cx-55, cy-55, cx+55, cy+55]
    d.arc(bbox_top, start=180, end=360, fill=(128, 216, 255, 255), width=20)
    # Middle line
    d.line([(cx-55, cy), (cx+55, cy)], fill=(128, 216, 255, 255), width=20)
    # Bottom arc
    bbox_bot = [cx-55, cy-55, cx+55, cy+55]
    d.arc(bbox_bot, start=0, end=180, fill=(128, 216, 255, 255), width=20)

    # Dots (decreasing)
    d.ellipse([cx+75, cy-18, cx+95, cy+2], fill=(128, 216, 255, 220))
    d.ellipse([cx+105, cy-14, cx+121, cy+2], fill=(128, 216, 255, 160))
    d.ellipse([cx+131, cy-11, cx+143, cy+1], fill=(128, 216, 255, 90))

    # Flip arc below
    d.arc([cx-70, cy+40, cx+70, cy+180], start=180, end=360,
          fill=(128, 216, 255, 80), width=6)

    img.save(os.path.join(OUTPUT_DIR, "icon_concept_d2.png"))
    print("Saved concept D2")


render_concept_b()
render_concept_c()
render_concept_e()
render_concept_d2()
print("All done!")
