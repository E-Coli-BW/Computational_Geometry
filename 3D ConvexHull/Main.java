// package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import acp.GO;
import acp.Real;
import hull3D.FastConvexHull3D;
import hull3D.FastConvexHull3D.HState;
import hull3D.FastConvexHull3D.STATE;
import hull3D.Triangle3D;
import hull3D.Vertex3D;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import pv.InputPoint3D;
import pv.PV3;

public class Main extends Application {

  Random random = new Random(1);

	static List<GO<PV3>> points = new ArrayList<GO<PV3>>();
	List<GO<PV3>> pointsOnParab = new ArrayList<GO<PV3>>();

	FastConvexHull3D hull = null;
	FastConvexHull3D hullParab = null;

	int speed = 2;

	private static final int INITIAL_POINTS = 100;

	private void makePoints(int n) {
		points.clear();

		while (n-- > 0) {
			double theta = random.nextDouble() * Math.PI;
			double phi = random.nextDouble() * 2 * Math.PI;
			double r = random.nextDouble() * (POINT_LENGTH * 0.8) + 0.2 * POINT_LENGTH;

			double x = Math.sin(theta) * Math.cos(phi) * r;
			double y = Math.sin(theta) * Math.sin(phi) * r;
			double z = Math.cos(theta) * r;

			points.add(new InputPoint3D(x, y, z));
		}

		System.out.println("speed: " + speed);

		hull = new FastConvexHull3D(points, speed);

	}

	private void makePointsOnParab(int n) {
		pointsOnParab.clear();

		while (n-- > 0) {
			double x = (random.nextDouble() - 0.5);
			double y = (random.nextDouble() - 0.5);
			double z = -(x * x + y * y);
			pointsOnParab.add(new InputPoint3D(x, z, y));
		}

		hullParab = new FastConvexHull3D(pointsOnParab, speed);

	}

	final Group root = new Group();
	final Group root1 = new Group();
	final Group root2 = new Group();

	final Xform axisGroup = new Xform();
	final Xform pointsXform = new Xform();
	final Xform triangleXform = new Xform();
	final Xform lineXform = new Xform();

	final Xform parabPlaneXform = new Xform();
	final Xform parabXform = new Xform();
	final Xform parabTriangleXform = new Xform();
	final Xform parabPointsXform = new Xform();
	final Xform parabLineXform = new Xform();

	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final Xform cameraXform = new Xform();
	final Xform cameraXform1 = new Xform();
	final Xform cameraXform2 = new Xform();

	private static final double CAMERA_INITIAL_DISTANCE = -550;
	private static final double CAMERA_INITIAL_X_ANGLE = 200;
	private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
	private static final double CAMERA_INITIAL_NEAR_CLIP = 0.1;
	private static final double CAMERA_INITIAL_FAR_CLIP = 10000.0;

	private static final double AXIS_LENGTH = 250.0;
	private static final double POINT_LENGTH = 0.5 * AXIS_LENGTH;

	private static final double CONTROL_MULTIPLIER = 0.1;
	private static final double SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.1;
	private static final double ROTATION_SPEED = 2.0;
	private static final double TRACK_SPEED = 0.3;

	private static final int PARAB_SUB = 10;

	double mousePosX;
	double mousePosY;
	double mouseOldX;
	double mouseOldY;
	double mouseDeltaX;
	double mouseDeltaY;

	private void buildCamera() {
		root.getChildren().add(cameraXform);
		cameraXform.getChildren().add(cameraXform1);
		cameraXform1.getChildren().add(cameraXform2);
		cameraXform2.getChildren().add(camera);
		cameraXform2.setRotateZ(180.0);

		camera.setNearClip(CAMERA_INITIAL_NEAR_CLIP);
		camera.setFarClip(CAMERA_INITIAL_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
		cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

	}

	private void buildAxes() {
		final PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);

		final PhongMaterial greenMaterial = new PhongMaterial();
		greenMaterial.setDiffuseColor(Color.DARKGREEN);
		greenMaterial.setSpecularColor(Color.GREEN);

		final PhongMaterial blueMaterial = new PhongMaterial();
		blueMaterial.setDiffuseColor(Color.DARKBLUE);
		blueMaterial.setSpecularColor(Color.BLUE);

		final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
		final Box yAxis = new Box(1, AXIS_LENGTH, 1);
		final Box zAxis = new Box(1, 1, AXIS_LENGTH);

		xAxis.setMaterial(redMaterial);
		yAxis.setMaterial(greenMaterial);
		zAxis.setMaterial(blueMaterial);

		axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
		axisGroup.setVisible(true);
		root1.getChildren().addAll(axisGroup);

	}

	PhongMaterial blueMaterial;
	PhongMaterial chosenMaterial;

	private void buildPoints() {

		blueMaterial = new PhongMaterial();
		blueMaterial.setDiffuseColor(Color.DARKBLUE);
		blueMaterial.setSpecularColor(Color.BLUE);

		chosenMaterial = new PhongMaterial();
		chosenMaterial.setDiffuseColor(Color.DARKRED);
		chosenMaterial.setSpecularColor(Color.RED);

		for (int i = 0; i < hull.getVs().size(); i++) {

			Vertex3D p = hull.getVs().get(i);

			Xform pointXform = new Xform();

			Sphere pointSphere = new Sphere(1.3);
			pointSphere.setMaterial(blueMaterial);

			pointXform.getChildren().add(pointSphere);

			pointXform.setTx(p.getP().xyz().x.approx());
			pointXform.setTy(p.getP().xyz().y.approx());
			pointXform.setTz(p.getP().xyz().z.approx());

			pointsXform.getChildren().add(pointXform);

		}

		root1.getChildren().addAll(pointsXform);

		for (int i = 0; i < hullParab.getVs().size(); i++) {

			Vertex3D p = hullParab.getVs().get(i);

			Xform pointXform = new Xform();

			Sphere pointSphere = new Sphere(1.3);
			pointSphere.setMaterial(blueMaterial);

			pointXform.getChildren().add(pointSphere);

			pointXform.setTx(p.getP().xyz().x.approx() * POINT_LENGTH);
			pointXform.setTy(p.getP().xyz().y.approx() * POINT_LENGTH);
			pointXform.setTz(p.getP().xyz().z.approx() * POINT_LENGTH);

			parabPointsXform.getChildren().add(pointXform);

		}

		root2.getChildren().addAll(parabPointsXform);

	}

	private void buildButtons() {

	}

	private void handleMouse(Scene scene, final Node root) {

		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				double modifier = 1.0;

				if (me.isControlDown()) {
					modifier = CONTROL_MULTIPLIER;
				}
				if (me.isShiftDown()) {
					modifier = SHIFT_MULTIPLIER;
				}
				if (me.isPrimaryButtonDown()) {
					cameraXform.ry.setAngle(cameraXform.ry.getAngle() + mouseDeltaX * modifier * ROTATION_SPEED); //
					cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * modifier * ROTATION_SPEED); // -
				} else if (me.isSecondaryButtonDown()) {
					double z = camera.getTranslateZ();
					double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
					camera.setTranslateZ(newZ);
				} else if (me.isMiddleButtonDown()) {
					cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED); // -
					cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED); // -
				}
			}
		}); // setOnMouseDragged
	} // handleMouse

	private void handleKeyboard(Scene scene, final Node root) {

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case Z:
					cameraXform2.t.setX(0.0);
					cameraXform2.t.setY(0.0);
					cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
					cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
					break;
				case X:
					axisGroup.setVisible(!axisGroup.isVisible());
					break;
				case V:
					pointsXform.setVisible(!pointsXform.isVisible());
					break;
				case S:
					step();
					break;
				case P:
					switchScene();
					break;
				} // switch
			} // handle()
		}); // setOnKeyPressed
	} // handleKeyboard()

	public class Shape3DTriangle extends TriangleMesh {

		public Shape3DTriangle(PV3 a, PV3 b, PV3 c) {
			float[] points = { (float) a.x.approx(), (float) a.y.approx(), (float) a.z.approx(), (float) b.x.approx(),
					(float) b.y.approx(), (float) b.z.approx(), (float) c.x.approx(), (float) c.y.approx(), (float) c.z.approx() };

			float[] texCoords = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };

			int[] faces = { 0, 0, 1, 1, 2, 2, 0, 0, 2, 2, 1, 1 };

			this.getPoints().setAll(points);
			this.getTexCoords().setAll(texCoords);
			this.getFaces().setAll(faces);
		}
	}

	public class Shape3DRectangle extends TriangleMesh {

		public Shape3DRectangle(PV3 a, PV3 b, PV3 c, PV3 d) {

			float[] points = { (float) a.x.approx(), (float) a.y.approx(), (float) a.z.approx(), (float) b.x.approx(),
					(float) b.y.approx(), (float) b.z.approx(), (float) c.x.approx(), (float) c.y.approx(), (float) c.z.approx(),
					(float) d.x.approx(), (float) d.y.approx(), (float) d.z.approx() };

			float[] texCoords = { 1, 1, // idx t0
					1, 0, // idx t1
					0, 1, // idx t2
					0, 0 // idx t3
			};

			// if you use the co-ordinates as defined in the above comment, it
			// will be all messed up
			// int[] faces = {
			// 0, 0, 1, 1, 2, 2,
			// 0, 0, 2, 2, 3, 3
			// };

			// try defining faces in a counter-clockwise order to see what the
			// difference is.
			// int[] faces = {
			// 2, 2, 1, 1, 0, 0,
			// 2, 2, 3, 3, 1, 1
			// };

			// try defining faces in a clockwise order to see what the
			// difference is.
			int[] faces = { 2, 3, 0, 2, 1, 0, 2, 3, 1, 0, 3, 1 };

			this.getPoints().setAll(points);
			this.getTexCoords().setAll(texCoords);
			this.getFaces().setAll(faces);
		}
	}

	public class Shape3DLineSegment extends Xform {

		public Shape3DLineSegment(PV3 p, PV3 q, Material lineMaterial) {

			PV3 ab = q.minus(p);
			PV3 mid_ab = p.times(Real.constant(0.5)).plus(q.times(Real.constant(0.5)));

			Cylinder c_ab = new Cylinder(0.5, ab.length().approx());
			PV3 abAxisPV3 = ab.unit().cross(PV3.Y);
			Point3D abAxis = new Point3D(abAxisPV3.x.approx(), abAxisPV3.y.approx(), abAxisPV3.z.approx());
			double angle = -Math.toDegrees(Math.acos(ab.unit().dot(PV3.Y).approx()));

			Translate t_ab = new Translate(mid_ab.x.approx(), mid_ab.y.approx(), mid_ab.z.approx());

			Rotate r_ab = new Rotate(angle, abAxis);

			c_ab.getTransforms().addAll(t_ab, r_ab);

			this.getChildren().addAll(c_ab);
		}

		public Shape3DLineSegment(PV3 p, PV3 q) {
			this(p, q, new PhongMaterial());
		}
	}

	public class Shape3DDottedLineSegment extends Xform {

		public Shape3DDottedLineSegment(PV3 p, PV3 q, Material lineMaterial, int dots) {

			PV3 ab = q.minus(p);
			PV3 mid_ab = p.times(Real.constant(0.5)).plus(q.times(Real.constant(0.5)));

			double length = ab.length().approx() / (2 * dots - 1);

			PV3 abAxisPV3 = ab.unit().cross(PV3.Y);
			Point3D abAxis = new Point3D(abAxisPV3.x.approx(), abAxisPV3.y.approx(), abAxisPV3.z.approx());
			double angle = -Math.toDegrees(Math.acos(ab.unit().dot(PV3.Y).approx()));
			Rotate r_ab = new Rotate(angle, abAxis);

			for (int i = 0; i < dots; i++) {

				Cylinder c_ab = new Cylinder(0.5, length);

				PV3 location = p.plus(ab.unit().times(Real.constant((2 * i + 0.5) * length)));

				Translate t_ab = new Translate(location.x.approx(), location.y.approx(), location.z.approx());

				c_ab.getTransforms().addAll(t_ab, r_ab);

				c_ab.setMaterial(lineMaterial);

				this.getChildren().addAll(c_ab);
			}

		}

		public Shape3DDottedLineSegment(PV3 p, PV3 q) {
			this(p, q, new PhongMaterial(Color.BLACK), 10);
		}
	}

	PhongMaterial triangleMaterial;
	PhongMaterial deadMaterial;

	private void addTriangle(PV3 a, PV3 b, PV3 c) {

		TriangleMesh mesh = new Shape3DTriangle(a, b, c);

		MeshView view = new MeshView(mesh);

		view.setMaterial(triangleMaterial);

		PhongMaterial lineMaterial = new PhongMaterial();

		PV3 vert[] = new PV3[] { a, b, c };

		Xform lines = new Xform();

		for (int i = 0; i < 3; i++) {

			PV3 p = vert[i];
			PV3 q = vert[(i + 1) % 3];

			lines.getChildren().addAll(new Shape3DLineSegment(p, q, lineMaterial));

		}

		triangleXform.getChildren().addAll(view);
		lineXform.getChildren().addAll(lines);

	}

	private void addTriangleParab(PV3 a, PV3 b, PV3 c) {

		Xform tri = new Xform();
		Xform trilines = new Xform();

		TriangleMesh mesh = new Shape3DTriangle(a, b, c);

		MeshView view = new MeshView(mesh);

		view.setMaterial(triangleMaterial);

		PhongMaterial lineMaterial = new PhongMaterial();

		PV3 vert[] = new PV3[] { a, b, c };

		Xform lines = new Xform();

		for (int i = 0; i < 3; i++) {

			PV3 p = vert[i];
			PV3 q = vert[(i + 1) % 3];

			lines.getChildren().addAll(new Shape3DLineSegment(p, q, lineMaterial));

		}

		tri.getChildren().add(view);

		mesh = new Shape3DTriangle(a, c, b);
		view = new MeshView(mesh);
		view.setMaterial(triangleMaterial);

		tri.getChildren().add(view);

		trilines.getChildren().add(lines);

		PV3 ap = new PV3(a.x, Real.constant((AXIS_LENGTH / 4) - 0.5), a.z);
		PV3 bp = new PV3(b.x, Real.constant((AXIS_LENGTH / 4) - 0.5), b.z);
		PV3 cp = new PV3(c.x, Real.constant((AXIS_LENGTH / 4) - 0.5), c.z);

		mesh = new Shape3DTriangle(ap, cp, bp);

		view = new MeshView(mesh);

		view.setMaterial(triangleMaterial);

		tri.getChildren().add(view);

		vert = new PV3[] { ap, bp, cp };

		lines = new Xform();

		for (int i = 0; i < 3; i++) {

			PV3 p = vert[i];
			PV3 q = vert[(i + 1) % 3];

			lines.getChildren().addAll(new Shape3DLineSegment(p, q, lineMaterial));

		}

		lines.getChildren().addAll(new Shape3DDottedLineSegment(a, ap, new PhongMaterial(Color.web("0x00000010")), 5));
		lines.getChildren().addAll(new Shape3DDottedLineSegment(b, bp, new PhongMaterial(Color.web("0x00000010")), 5));
		lines.getChildren().addAll(new Shape3DDottedLineSegment(c, cp, new PhongMaterial(Color.web("0x00000010")), 5));

		trilines.getChildren().add(lines);

		ap = new PV3(a.x, Real.constant((AXIS_LENGTH / 4) + 0.5), a.z);
		bp = new PV3(b.x, Real.constant((AXIS_LENGTH / 4) + 0.5), b.z);
		cp = new PV3(c.x, Real.constant((AXIS_LENGTH / 4) + 0.5), c.z);

		mesh = new Shape3DTriangle(ap, bp, cp);

		view = new MeshView(mesh);

		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(Color.TEAL);
		view.setMaterial(mat);

		tri.getChildren().add(view);

		vert = new PV3[] { ap, bp, cp };

		lines = new Xform();

		for (int i = 0; i < 3; i++) {

			PV3 p = vert[i];
			PV3 q = vert[(i + 1) % 3];

			lines.getChildren().addAll(new Shape3DLineSegment(p, q, lineMaterial));

		}

		trilines.getChildren().add(lines);

		parabTriangleXform.getChildren().addAll(tri);
		parabLineXform.getChildren().addAll(trilines);

	}

	HState hState = null;
	HState hStatePrev = null;

	Iterator<HState> hIterator;

	HState pState = null;
	HState pStatePrev = null;

	Iterator<HState> pIterator;

	boolean computed = false;

	private void makeComputedGraphics() {
		hull.makeHull();

		for (int i = 0; i < hull.getTs().size(); i++) {
			Triangle3D t = hull.getTs().get(i);
			addTriangle(t.v[0].p.xyz(), t.v[1].p.xyz(), t.v[2].p.xyz());
		}
	}

	private void stepHullNew() {
		if (hState == null) {
			hIterator = hull.states.iterator();
			for (int i = 0; i < hull.getTs().size(); i++) {
				triangleXform.getChildren().get(i).setVisible(false);
				lineXform.getChildren().get(i).setVisible(false);
			}
		}

		if (hIterator.hasNext()) {
			hStatePrev = hState;
			hState = hIterator.next();

		} else {
			hState = hStatePrev = null;
		}

		if (hState != null) {

			for (int i = 0; i < hState.liveDead.size(); i++) {

				switch (hState.liveDead.get(i)) {
				case 0:
					triangleXform.getChildren().get(i).setVisible(false);
					lineXform.getChildren().get(i).setVisible(false);
					break;
				case 1:
					((MeshView) triangleXform.getChildren().get(i)).setMaterial(deadMaterial);
					break;
				case 2:
					triangleXform.getChildren().get(i).setVisible(true);
					lineXform.getChildren().get(i).setVisible(true);
					((MeshView) triangleXform.getChildren().get(i)).setMaterial(triangleMaterial);
					break;
				}

			}

			if (hStatePrev != null && hStatePrev.vIndex != -1) {
				Sphere sp = ((Sphere) ((Xform) pointsXform.getChildren().get(hStatePrev.vIndex)).getChildren().get(0));
				sp.setMaterial(blueMaterial);
				sp.setRadius(1.3);
			}

			if (hState.vIndex != -1) {
				Sphere sp = ((Sphere) ((Xform) pointsXform.getChildren().get(hState.vIndex)).getChildren().get(0));
				sp.setMaterial(chosenMaterial);
				sp.setRadius(2.5);
			}

		}

	}

	private void stepNew() {

		if (root1.isVisible()) {

			if (!computed) {
				makeComputedGraphics();
				computed = true;
			}

			stepHullNew();
		} else {
			stepParab();
		}

	}

	private void step() {
		if (root1.isVisible())
			stepHull();
		else
			stepParab();
	}

	private void stepParab() {
		hullParab.step();

		while (hullParab.getState() != STATE.DEFAULT)
			hullParab.step();

		int tSize = parabTriangleXform.getChildren().size();

		List<Triangle3D> list = hullParab.getTs();

		for (int i = tSize; i < list.size(); i++) {
			Triangle3D t = list.get(i);
			if (t.upwardNormal()) {

				PV3 a = t.v[0].p.xyz().times(Real.constant(POINT_LENGTH));
				PV3 b = t.v[1].p.xyz().times(Real.constant(POINT_LENGTH));
				PV3 c = t.v[2].p.xyz().times(Real.constant(POINT_LENGTH));

				addTriangleParab(a, b, c);

			} else {
				parabTriangleXform.getChildren().add(new Xform());
				parabLineXform.getChildren().add(new Xform());
			}
		}

		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).live) {
				parabTriangleXform.getChildren().get(i).setVisible(false);
				parabLineXform.getChildren().get(i).setVisible(false);
			}
		}

	}

	private void stepHull() {

		hull.step();

		int tSize = triangleXform.getChildren().size();

		List<Triangle3D> list = hull.getTs();

		for (int i = tSize; i < list.size(); i++) {
			Triangle3D t = list.get(i);
			addTriangle(t.v[0].p.xyz(), t.v[1].p.xyz(), t.v[2].p.xyz());
			// addTriangle(t.v[0].p.xyz(), t.v[2].p.xyz(),
			// t.v[1].p.xyz());
		}

		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).live) {
				if (hull.getState() == STATE.BUILDING || hull.getState() == STATE.HORIZON) {
					((MeshView) triangleXform.getChildren().get(i)).setMaterial(deadMaterial);
				} else {
					triangleXform.getChildren().get(i).setVisible(false);
					lineXform.getChildren().get(i).setVisible(false);
				}
			}
		}

		if (hull.getState() != STATE.DEFAULT) {
			((Sphere) ((Xform) pointsXform.getChildren().get(hull.getStep())).getChildren().get(0))
					.setMaterial(chosenMaterial);
			((Sphere) ((Xform) pointsXform.getChildren().get(hull.getStep())).getChildren().get(0)).setRadius(2.5);
		} else {
			((Sphere) ((Xform) pointsXform.getChildren().get(hull.getStep() - 1)).getChildren().get(0))
					.setMaterial(blueMaterial);
			((Sphere) ((Xform) pointsXform.getChildren().get(hull.getStep() - 1)).getChildren().get(0)).setRadius(1.3);
		}

	}

	public void buildTriangles() {
		triangleMaterial = new PhongMaterial();
		triangleMaterial.setDiffuseColor(Color.web("0x008080c0"));
		triangleMaterial.setSpecularColor(Color.BLACK);

		deadMaterial = new PhongMaterial();
		deadMaterial.setDiffuseColor(Color.RED);
		deadMaterial.setSpecularColor(Color.RED);

		root1.getChildren().addAll(triangleXform);
		root1.getChildren().addAll(lineXform);

		root2.getChildren().add(parabTriangleXform);
		root2.getChildren().add(parabLineXform);

	}

	private void buildParab() {

		double dd = POINT_LENGTH / PARAB_SUB;

		for (int i = 0; i < PARAB_SUB; i++) {
			for (int j = 0; j < PARAB_SUB; j++) {
				double x = (i * POINT_LENGTH / PARAB_SUB) - 0.5 * POINT_LENGTH;
				double y = (j * POINT_LENGTH / PARAB_SUB) - 0.5 * POINT_LENGTH;
				double xp = x + dd;
				double yp = y + dd;

				PV3 a = PV3.constant(x, -(x * x + y * y) / POINT_LENGTH, y);
				PV3 b = PV3.constant(xp, -(xp * xp + y * y) / POINT_LENGTH, y);
				PV3 c = PV3.constant(x, -(x * x + yp * yp) / POINT_LENGTH, yp);
				PV3 d = PV3.constant(xp, -(xp * xp + yp * yp) / POINT_LENGTH, yp);

				TriangleMesh mesh = new Shape3DRectangle(a, c, b, d);
				MeshView view = new MeshView(mesh);
				view.setMaterial(triangleMaterial);

				parabXform.getChildren().add(view);

				mesh = new Shape3DRectangle(a, b, c, d);
				view = new MeshView(mesh);
				view.setMaterial(triangleMaterial);

				parabXform.getChildren().add(view);
			}
		}

		root2.getChildren().add(parabXform);

	}

	private void buildPlane() {

		double x = -AXIS_LENGTH / 2;
		double y = -AXIS_LENGTH / 2;
		double xp = -x;
		double yp = -y;

		PV3 a = PV3.constant(x, AXIS_LENGTH / 4, y);
		PV3 b = PV3.constant(xp, AXIS_LENGTH / 4, y);
		PV3 c = PV3.constant(x, AXIS_LENGTH / 4, yp);
		PV3 d = PV3.constant(xp, AXIS_LENGTH / 4, yp);

		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(Color.DARKGRAY);

		TriangleMesh mesh = new Shape3DRectangle(a, c, b, d);
		MeshView view = new MeshView(mesh);
		view.setMaterial(mat);

		parabPlaneXform.getChildren().add(view);

		mesh = new Shape3DRectangle(a, b, c, d);
		view = new MeshView(mesh);
		view.setMaterial(mat);

		parabPlaneXform.getChildren().add(view);

		root2.getChildren().add(parabPlaneXform);
	}

	private void switchScene() {
		if (root1.isVisible()) {
			root1.setVisible(false);
			root2.setVisible(true);
		} else {
			root1.setVisible(true);
			root2.setVisible(false);
		}
	}

	@Override
	public void start(Stage primaryStage) {

		makePoints(INITIAL_POINTS);
		makePointsOnParab(INITIAL_POINTS);

		buildCamera();
		buildAxes();
		buildPoints();
		buildTriangles();

		buildParab();
		buildPlane();
		buildButtons();

		root2.setVisible(false);
		root.getChildren().addAll(root1, root2);

		SubScene subScene = new SubScene(root, 1024, 768, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.GRAY);
		subScene.setCamera(camera);

		// 2D
		BorderPane pane = new BorderPane();
		pane.setCenter(subScene);

		Button button1 = new Button("Step");
		button1.setOnAction(e -> {
			stepNew();
		});

		Button button2 = new Button("Switch Scene");
		button2.setOnAction(e -> {
			switchScene();
		});

		final Pane leftSpacer = new Pane();
		HBox.setHgrow(leftSpacer, Priority.SOMETIMES);

		final Pane rightSpacer = new Pane();
		HBox.setHgrow(rightSpacer, Priority.SOMETIMES);

		ToolBar toolBar = new ToolBar(leftSpacer, button1, button2, rightSpacer);
		toolBar.setOrientation(Orientation.HORIZONTAL);

		pane.setTop(toolBar);
		pane.setPrefSize(1024, 768);

		Scene scene = new Scene(pane);

		primaryStage.setScene(scene);
		primaryStage.setTitle("3D Convex Hull");
		primaryStage.show();

		handleKeyboard(scene, root);
		handleMouse(scene, root);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
