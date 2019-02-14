package alvastudio.com;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.omg.PortableInterceptor.Interceptor;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture canosBaixo;
	private Texture canosTopo;
	private Texture gameOver;
	private Texture fundo;
	private BitmapFont fonte;
	private BitmapFont mensagemReniciar;
	private Random numerosAleatorios;
	//private ShapeRenderer shape;
	private Circle passaroCirculo;
	private Rectangle canoTopoRect;
	private Rectangle canoBaixoRect;
	//Atributos de conig - int
	private int movimento = 0;
	private int pontuacao = 0;
	private int status = 0; // 0 = jogo não iniciado --- 1 = jogo iniciado --- 2 = game over!
	//Atributos de conig - float
	private float largura;
	private float altura;
	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoVerticalInicial;
	private float posicaoMovCanoHorizontal;
	private float espacoEntreCanos;
	private float alturaEntreCanosRandom;
	private float delta;
	//Atributos de conig - boolean
    private boolean marcouPonto;
    //Câmera & ViewPort
	private OrthographicCamera camera;
	private Viewport viewport;
	private static final float VIRTUAL_WIDTH = 768;
	private static final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		//inicializa e cria o jogo
		batch = new SpriteBatch();
		//shape = new ShapeRenderer();

        fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(8);

		mensagemReniciar = new BitmapFont();
		mensagemReniciar.setColor(Color.WHITE);
		mensagemReniciar.getData().setScale(3);

		numerosAleatorios = new Random();
		passaros  = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		passaroCirculo = new Circle();
        canoTopoRect = new Rectangle();
        canoBaixoRect = new Rectangle();

		canosTopo = new Texture("cano_topo_maior.png");
		canosBaixo = new Texture("cano_baixo_maior.png");
		gameOver = new Texture("game_over.png");
		fundo = new Texture("fundo.png");

		/**
		 * Configurando câmera e viewPort
		 * */
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		largura = VIRTUAL_WIDTH;
		altura = VIRTUAL_HEIGHT;
		posicaoVerticalInicial = altura / 2;
		posicaoMovCanoHorizontal = largura;
		espacoEntreCanos = 252;
	}

	@Override
	public void render () {

		camera.update();
		//lipar frames
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//chamado de tempos em tempos para renderizar o jogo
		delta = Gdx.graphics.getDeltaTime();
		variacao+= delta * 10;
		if (variacao >2) variacao=0;
		//------------------------------------------------------------------------------------------------------------------//
		if ( status == 0 ){
			if ( Gdx.input.justTouched() ) status = 1;
		}else {

			velocidadeQueda++;

			//--------------------------------------------------------------------------------------------------------------//
			if (posicaoVerticalInicial > 0 || velocidadeQueda < 0)
				posicaoVerticalInicial -= velocidadeQueda;
			//--------------------------------------------------------------------------------------------------------------//

			if (status == 1){
				posicaoMovCanoHorizontal -= delta * 390;
				//--------------------------------------------------------------------------------------------------------------//
				if (Gdx.input.justTouched()) velocidadeQueda = -15;
				//--------------------------------------------------------------------------------------------------------------//
				if (posicaoMovCanoHorizontal < -canosTopo.getWidth()) {
					posicaoMovCanoHorizontal = largura;
					alturaEntreCanosRandom = numerosAleatorios.nextInt(500) - 250;
					marcouPonto = false;
				}
				//-- Verificar pontuação ----------------------------------------------------------------------------------------//
				if (posicaoMovCanoHorizontal < 30) {
					if (!marcouPonto) pontuacao++; marcouPonto = true;
				}
			}else{
				if (Gdx.input.isTouched()){
					status = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoVerticalInicial = altura/2;
					posicaoMovCanoHorizontal = largura;
				}
			}

		}

		//config câmera!
        batch.setProjectionMatrix( camera.combined );

		batch.begin();

		batch.draw( fundo,0,0, largura , altura );
		batch.draw( canosTopo,posicaoMovCanoHorizontal,altura / 2 + espacoEntreCanos/2 + alturaEntreCanosRandom);
		batch.draw( canosBaixo,posicaoMovCanoHorizontal,altura / 2 - canosBaixo.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandom);
		batch.draw( passaros[ (int) variacao], 30,posicaoVerticalInicial );
        fonte.draw( batch, String.valueOf( pontuacao ), largura/2, altura - 70 );

        passaroCirculo.set(30 + passaros[0].getWidth()/2,
                posicaoVerticalInicial + passaros[0].getHeight()/2, passaros[0].getWidth() / 2 );

		canoTopoRect.set(posicaoMovCanoHorizontal, altura / 2 + espacoEntreCanos/2 + alturaEntreCanosRandom ,
				canosTopo.getWidth(), canosTopo.getHeight());

		canoBaixoRect.set(posicaoMovCanoHorizontal, altura / 2 - canosBaixo.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandom ,
				canosBaixo.getWidth(), canosBaixo.getHeight());

        if(status == 2){
            batch.draw(gameOver, largura/2 - gameOver.getWidth()/2, altura/2);
            mensagemReniciar.draw(batch,"Toque para reiniciar",largura/2 - 200 ,altura/2 - gameOver.getHeight()/2);
        }

		batch.end();

		/*Shape
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius);
        shape.rect(canoTopoRect.x,canoTopoRect.y,canoTopoRect.width,canoTopoRect.height);
		shape.rect(canoBaixoRect.x,canoBaixoRect.y,canoBaixoRect.width,canoBaixoRect.height);
        shape.setColor(Color.BLUE);
        shape.end();
        */

		//Colisão
		if( Intersector.overlaps(passaroCirculo, canoTopoRect)
				|| Intersector.overlaps(passaroCirculo, canoBaixoRect)
				|| posicaoVerticalInicial <=0
				|| posicaoVerticalInicial >= altura ){
			status = 2;
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}
}
