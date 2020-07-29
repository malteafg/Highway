# Highway Architect
![](./media/logo.png)

## Project about highways

## Milestones
- Usable and visible UI
- Green plane on white background, movable camera by mouse input with restrictions

## Todo
https://trello.com/b/5XTbD0qW/highway-architect

## Readable code
- Always write return types on defs
- Let vars and vals have names of at least three letters unless used in a simple structure (as an iterator or in a fold for instance)
- Always add empty parenthesis such as to distinguish between defs and vars/vals unless accessor def
- Use _ instead of null when first initializing
- Don't call an Array array, call it what it is

## Rules for doublesnapping
- The art of generating a road between two existing nodes.
- Only allowed if dot > 0
- Two cases:
  - Arc if the rays cross
    - If the the angle between the direction vectors are < 90°, they cannot be shorter than halft of min_curve_length
    - Else the minimum length of the direction vectors is antiDot() * min_curve_length / 2
    - ideal vector length = min(2/3 * dist * (1-cos)/sin, (rayIntersection - vector_origin).length)
  - S-curve if the rays do not cross
