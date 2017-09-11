(function(Lunar, window, undefined) {
    const DEG_TO_RAD = Math.PI / 180.0;
        Lunar.PixiCube = class {

    	constructor({
            front,
            back,
            left,
            right,
            top,
            bottom,
            centerX = 400,
            centerY = 300,
            centerZ = 50,
            halfwidth = 10,
            halfheight = 10,
            halfdepth = 10,
        }) {
            const displayList = new PIXI.display.DisplayList();
            const group = new PIXI.display.DisplayGroup(0, true);

            const container = new PIXI.Container();
            container.displayList = displayList;

            const cFront = new PIXI.projection.Sprite2d(front);
            const cBack = new PIXI.projection.Sprite2d(back);
            const cLeft = new PIXI.projection.Sprite2d(left);
            const cRight = new PIXI.projection.Sprite2d(right);
            const cTop =  new PIXI.projection.Sprite2d(top);
            const cBottom = new PIXI.projection.Sprite2d(bottom);

            cFront.zOrder = 6;
            cBack.zOrder = 1;
            cTop.zOrder = 5;
            cLeft.zOrder = 2;
            cRight.zOrder = 4;
            cBottom.zOrder = 2;

            cFront.displayGroup = group;
            cBack.displayGroup = group;
            cTop.displayGroup = group;
            cLeft.displayGroup = group;
            cRight.displayGroup = group;
            cBottom.displayGroup = group;

            container.addChild(cFront);
            container.addChild(cBack);
            container.addChild(cRight);
            container.addChild(cTop);
            container.addChild(cLeft);
            container.addChild(cBottom);

            this._container = container;
            this._tmp = mat4.create();

            this._localTransform = mat4.create();
            this._worldTransform = mat4.create();
            //this._projection = mat4.create();
            this._combined = mat4.create();

            //this.rotate3Deg(20, [1,0,0]);
            //this.rotate3Deg(20, [0,0,1]);
            this.translate3(centerX,centerY,centerZ);

            //mat4.perspective(this._projection, 60 * DEG_TO_RAD, 16/9, 1, 1000);
            this._frontTopLeft     = vec4.create();
            this._frontTopRight    = vec4.create();
            this._frontBottomLeft  = vec4.create();
            this._frontBottomRight = vec4.create();
            this._backTopLeft      = vec4.create();
            this._backTopRight     = vec4.create();
            this._backBottomLeft   = vec4.create();
            this._backBottomRight  = vec4.create();
            this.setDimensions(halfwidth, halfheight, halfdepth);

            this._vFrontTopLeft     = vec4.create();
            this._vFrontTopRight    = vec4.create();
            this._vFrontBottomLeft  = vec4.create();
            this._vFrontBottomRight = vec4.create();
            this._vBackTopLeft      = vec4.create();
            this._vBackTopRight     = vec4.create();
            this._vBackBottomLeft   = vec4.create();
            this._vBackBottomRight  = vec4.create();

            this.update();
        }

        setDimensions(halfwidth, halfheight, halfdepth) {
            vec4.set(this._frontTopLeft,    -halfwidth,  halfdepth, -halfheight, 1);
            vec4.set(this._frontTopRight,    halfwidth,  halfdepth, -halfheight, 1);
            vec4.set(this._frontBottomLeft, -halfwidth,  halfdepth,  halfheight, 1);
            vec4.set(this._frontBottomRight, halfwidth,  halfdepth,  halfheight, 1);
            vec4.set(this._backTopLeft,     -halfwidth, -halfdepth, -halfheight, 1);
            vec4.set(this._backTopRight,     halfwidth, -halfdepth, -halfheight, 1);
            vec4.set(this._backBottomLeft,  -halfwidth, -halfdepth,  halfheight, 1);
            vec4.set(this._backBottomRight,  halfwidth, -halfdepth,  halfheight, 1);
        }

        get container() {
            return this._container;
        }

        translate3(dx,dy,dz) {
            mat4.translate(this._worldTransform, this._worldTransform, [dx,dz,dy]);
            return this;
        }
        
        scale3(factors) {
            if (Array.isArray(factors))
                mat4.scale(this._localTransform, this._localTransform, factors);                
            else
                mat4.scale(this._localTransform, this._localTransform, [factors,factors,factors]);
            return this;
        }

        rotate3Rad(radians, axis) {
            mat4.rotate(this._localTransform, this._localTransform, radians, axis);
            return this;
        }

        rotate3Deg(degrees, axis) {
            mat4.rotate(this._localTransform, this._localTransform, degrees * DEG_TO_RAD, axis);
            return this;
        }

        preRotate3Deg(degrees, axis) {
        	mat4.fromRotation(this._tmp, degrees * DEG_TO_RAD, axis);
        	mat4.multiply(this._localTransform, this._localTransform, this._tmp);
            return this;
        }
        
        postRotate3Deg(degrees, axis) {
        	mat4.fromRotation(this._tmp, degrees * DEG_TO_RAD, axis);
        	mat4.multiply(this._localTransform, this._tmp, this._localTransform);
            return this;
        }

        set localTransform3(localTransform) {
        	mat4.copy(this._localTransform, localTransform);
            return this;
        }
        
        resetLocalTransform() {
        	mat4.identity(this._localTransform);
        }
        
        resetWorldTransform() {
        	mat4.identity(this._worldTransform);
        }

        get localTransform3() {
        	return mat4.clone(this._localTransform);
        }

        set worldTransform3(worldTransform) {
            mat4.copy(this._worldTransform, worldTransform);
            return this;
        }

        get worldTransform3() {
            return mat4.clone(this._worldTransform);
        }

        update() {
            //mat4.multiply(this._combined, this._projection, this._worldTransform);
            //mat4.multiply(this._combined, this._combined, this._localTransform);
            mat4.multiply(this._combined, this._worldTransform, this._localTransform);

            vec4.transformMat4(this._vBackBottomLeft, this._backBottomLeft, this._combined);
            vec4.transformMat4(this._vBackBottomRight, this._backBottomRight, this._combined);
            vec4.transformMat4(this._vBackTopLeft, this._backTopLeft, this._combined);
            vec4.transformMat4(this._vBackTopRight, this._backTopRight, this._combined);
            vec4.transformMat4(this._vFrontBottomLeft, this._frontBottomLeft, this._combined);
            vec4.transformMat4(this._vFrontBottomRight, this._frontBottomRight, this._combined);
            vec4.transformMat4(this._vFrontTopLeft, this._frontTopLeft, this._combined);
            vec4.transformMat4(this._vFrontTopRight, this._frontTopRight, this._combined);

            this._divide(this._vBackBottomLeft);
            this._divide(this._vBackBottomRight);
            this._divide(this._vBackTopLeft);
            this._divide(this._vBackTopRight);
            this._divide(this._vFrontBottomLeft);
            this._divide(this._vFrontBottomRight);
            this._divide(this._vFrontTopLeft);
            this._divide(this._vFrontTopRight);

            // front
            this._setCorner(0, this._vFrontTopLeft, this._vFrontTopRight,
                this._vFrontBottomRight, this._vFrontBottomLeft);

            // back
            this._setCorner(1, this._vBackTopLeft, this._vBackTopRight,
                this._vBackBottomRight, this._vBackBottomLeft);

            // right
            this._setCorner(2, this._vFrontTopRight, this._vBackTopRight,
                this._vBackBottomRight, this._vFrontBottomRight);

            // top
            this._setCorner(3, this._vBackTopLeft, this._vBackTopRight,
                this._vFrontTopRight, this._vFrontTopLeft);

            // left
            this._setCorner(4, this._vBackTopLeft, this._vFrontTopLeft,
                this._vFrontBottomLeft, this._vBackBottomLeft);

             // bottom
            this._setCorner(5, this._vFrontBottomLeft, this._vFrontBottomRight,
                this._vBackBottomRight, this._vBackBottomLeft);
        }   

        _divide(vec) {
            const s = 1 / vec[3];           
            vec[0] *= s;
            vec[1] *= s;
            vec[2] *= s;            
        }

        _setCorner(index, vecTopLeft, vecTopRight, vecBottomRight, vecBottomLeft) {
            const child = this._container.children[index];
            const vertexData = child.vertexData;
            child.zOrder = -this._min(vecTopLeft[1], vecTopRight[1], vecBottomLeft[1], vecBottomRight[1]);
            vertexData[0] = vecTopLeft[0];
            vertexData[1] = vecTopLeft[2];
            vertexData[3] = vecTopRight[0];
            vertexData[4] = vecTopRight[2];
            vertexData[6] = vecBottomRight[0];
            vertexData[7] = vecBottomRight[2];
            vertexData[9] = vecBottomLeft[0];
            vertexData[10] = vecBottomLeft[2];
        }

        _min(...numbers) {
            let min = numbers[0];
            for (let i = numbers.length; i --> 1;)
                if (numbers[i] < min)
                    min = numbers[i];
            return min;
        }

        _max(...numbers) {
            let max = numbers[0];
            for (let i = numbers.length; i --> 1;)
                if (numbers[i] > max)
                    max = numbers[i];
            return max;
        }
    };
})(window.Lunar, window);
