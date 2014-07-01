/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.pipeline.core.matrix.util;

public class Maths {

   /** sqrt(a^2 + b^2) without under/overflow. **/

   public static double hypot(double a, double b) {
      double r;
      if (Math.abs(a) > Math.abs(b)) {
         r = b/a;
         r = Math.abs(a)*Math.sqrt(1+r*r);
      } else if (b != 0) {
         r = a/b;
         r = Math.abs(b)*Math.sqrt(1+r*r);
      } else {
         r = 0.0;
      }
      return r;
   }
}
