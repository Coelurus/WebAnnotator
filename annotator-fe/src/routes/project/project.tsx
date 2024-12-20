import axios from 'axios';
import { ChangeEvent, useEffect, useState, useRef, useLayoutEffect } from "react";
import { Form, LoaderFunction, useLoaderData } from "react-router-dom";
import { fetchProject } from "../../persistence/fetcher/fetcher";
import { PredictionTriple, ProjectResponse } from "../../persistence/model/responses";
import { Annotation, Label } from "../../persistence/model/responses";
import "../../styles/galery.css";
export const loader: LoaderFunction = async ({ params }) => {
    const project = await fetchProject(Number(params.projectId));
    return project;
}

export default function Project() {
    const UNDEFINED = -1;
    const LEFT_BUTTON = 0;
    const RIGHT_BUTTON = 2;
    const DEFAULT_IMAGE_SIZE = 50;

    const [pageNum, setPageNum] = useState<number>(0);
    const [frameCount, setFrameCount] = useState<number>(0);
    const [startIndex, setStartIndex] = useState<number>(UNDEFINED);
    const [endIndex, setEndIndex] = useState<number>(UNDEFINED);
    const [selectedFrames, setSelectedFrames] = useState<Annotation[]>([]);
    const project = useLoaderData() as ProjectResponse;
    const [imagesPerPage, setImagesPerPage] = useState<number>(100);
    const [labels, setLabels] = useState<Label[]>([]);
    const [currentLabel, setCurrentLabel] = useState<Label>();
    const [pressedButton, setPressedButton] = useState<number>(UNDEFINED);

    const [imageSize, setImageSize] = useState<number>(DEFAULT_IMAGE_SIZE);

    // frameId to Label mapping
    const [frameLabels, setFrameLabels] = useState<Record<number, Label | null>>({});

    const gridRef = useRef<HTMLDivElement>(null);

    const handleSliderChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setImageSize(Number(event.target.value));
    };

    useLayoutEffect(() => {
        const updateImagesPerPage = () => {
            if (gridRef.current) {
                const gridWidth = gridRef.current.clientWidth;
                const gridHeight = gridRef.current.clientHeight;
                const columns = Math.floor(gridWidth / imageSize);
                const rows = Math.floor(gridHeight / imageSize);
                setImagesPerPage(columns * rows);
                console.log(gridWidth, gridHeight);
                
            }
        };

        updateImagesPerPage();
        window.addEventListener("resize", updateImagesPerPage);
        return () => window.removeEventListener("resize", updateImagesPerPage);
    }, [imageSize]);

    useEffect(() => {
        fetch('/api/projects/' + project.id + '/frame/count')
            .then((response) => response.json())
            .then((data) => setFrameCount(data.count))
            .catch((error) => console.error('Error fetching frame count:', error));


        fetch(`/api/projects/${project.id}/annotations`)
            .then((response) => {
                return response.json();
            })
            .then((data: Annotation[]) => {
                //const annotatedFrames = data.map(annotation => annotation.frameId);
                setSelectedFrames(data);
            })
            .catch((error) => console.error('Error fetching annotations:', error));

    }, [project.id]);

    useEffect(() => {
        fetch(`/api/labels`)
            .then((response) => response.json())
            .then((data) => {
                setLabels(data);
                setCurrentLabel(data[0]);
            })
            .catch((error) => console.error('Error fetching labels:', error));
        }, []);

    const nextPage = () => {
        if ((pageNum + 1) * imagesPerPage < frameCount) {
            setPageNum(pageNum + 1);
        }
    };

    const prevPage = () => {
        if (pageNum > 0) {
            setPageNum(pageNum - 1);
        }
    };

    const handleMouseDown = (event: React.MouseEvent, frameId: number) => {
        setPressedButton(event.nativeEvent.button);
        setStartIndex(frameId);
    };

    const handleMouseUp = () => {
        if(startIndex === UNDEFINED){
            return;
        }

        if(!currentLabel) {
            //TODO - add better alerting...
            alert("Label not chosen...");
            return;
        }

        var lowerIndex: number;
        var higherIndex: number;
        if( startIndex > endIndex ){
            lowerIndex = endIndex;
            higherIndex = startIndex;
        } else {
            lowerIndex = startIndex;
            higherIndex = endIndex;
        }

        if(pressedButton === RIGHT_BUTTON) {
            fetch(`/api/projects/${project.id}/erase/${lowerIndex}/${higherIndex}`, {
                method: 'POST',
            });

            const withoutErased = selectedFrames.filter(annotation => annotation.frameId < lowerIndex || annotation.frameId > higherIndex);
            setSelectedFrames(withoutErased);
        }
        
        if(currentLabel && pressedButton === LEFT_BUTTON) {
            fetch(`/api/projects/${project.id}/annotate/${lowerIndex}/${higherIndex}/label/${currentLabel?.id}`, {
                method: 'POST',
            });

            const framesToAdd: Annotation[] = [];
            for (let index = lowerIndex; index <= higherIndex; index++) {
                if (lowerIndex != UNDEFINED && !selectedFrames.some(frame => frame.frameId === index)) {
                    framesToAdd.push({ frameId: index, labelId: currentLabel.id });
                }
            }
            setSelectedFrames(selectedFrames => [...selectedFrames, ...framesToAdd]);                
        }

        setStartIndex(UNDEFINED);
        setEndIndex(UNDEFINED);
      };

    
    const handleMouseOver = (frameId: number) => {
        setEndIndex(frameId);
    };

    const handleLabelChange = (event: ChangeEvent<HTMLSelectElement>) => {
        const selectedLabelId = Number(event.target.selectedOptions[0].getAttribute("label-id"));
        const selectedLabelName = event.target.selectedOptions[0].getAttribute("label-name");
        const selectedLabelColor = event.target.selectedOptions[0].getAttribute("label-color");
        if (selectedLabelName && selectedLabelColor) {
            setCurrentLabel({
                id: selectedLabelId,
                label: selectedLabelName,
                color: selectedLabelColor
            });
        }
    }

    const preventDragHandler = (e: Event) => {
        e.preventDefault();
    }

    const handleAddLabel = () => {
        const element = document.getElementById('new-label-text-input') as HTMLInputElement;

        if (element) {
            fetch(`/api/labels/${element.value}`, {
                method: 'POST',
            })
                .then((response) => response.json())
                .then((data) => {
                    setLabels([...labels, data]);
                    setCurrentLabel(data);
                })
                .catch((error: Error) => {
                    alert('Error creating label: ' + element.value + '\nLabel name already exists.')
                });;
        } else {
            alert("Issue occurred");
        }
        
    }

    const startPosition = pageNum * imagesPerPage + 1;
    const endPosition = Math.min(startPosition + imagesPerPage - 1, frameCount);

    const imagePositions = Array.from(
        { length: endPosition - startPosition + 1 },
        (_, i) => startPosition + i
    );

    const selectedImageStyle = (index: number) => {
        const frame = selectedFrames.find(frame => frame.frameId === index);
        return {
            width: `${imageSize}px`,
            height: `${imageSize}px`,
            borderColor: frame ? labels[frame.labelId].color : '',
            borderWidth: "5px",
            borderStyle: "solid"
        }
    };

    const trainAI = async (projectId: number) => {
        try {
            
            const { data: response } = await axios.post<PredictionTriple[]>(`/api//projects/${projectId}/trainAI`);
            response.forEach(element => {
                const imageFrameElement = document.getElementById(`image-frame-indicator-${element.frameId}`);
                const color = labels.filter(label => label.id == Number(element.label))[0].color
                if(imageFrameElement){
                    imageFrameElement.textContent = "X"
                    imageFrameElement.style.color = color
                }
                
            });
            
            alert('Training finished!');
        } catch (error) {
            alert('Training failed');
        }
    };

    return (
        <div 
            onMouseUp={() => handleMouseUp()}
            onContextMenu={(event) => {event.preventDefault();}}
        >
            <h1>{project ? project.projectName : 'No project found'}</h1>

            <div className="slider-container">
                <label htmlFor="image-size-slider">Image Size: {imageSize}px</label>
                <input
                    type="range"
                    id="image-size-slider"
                    min="30"
                    max="200"
                    value={imageSize}
                    onChange={handleSliderChange}
                />
            </div>

            <select 
                name="label" 
                id="label-select"
                value={currentLabel?.label || ''}
                onChange={handleLabelChange}
            >
                {labels.map((label) => (
                    <option 
                        value={label.label} 
                        key={"label_" + label.label}
                        label-id={label.id} 
                        label-name={label.label}
                        label-color={label.color}
                    > 
                        {label.label}
                    </option>
                ))}
            </select>

            <input type="text" id="new-label-text-input" placeholder="New label"></input>
            <button onClick={() => handleAddLabel()}>Add label</button>

            <button onClick={() => trainAI(project.id)}>Train AI</button>

            <div 
                className="image-grid"
                ref={gridRef}
            >
                {imagePositions.map(position => (
                    <div id={`image-frame-wrapper-${position}`}>
                        <div className='image-frame-indicator' id={`image-frame-indicator-${position}`}></div>
                        <img
                            id={`image-frame-${position}`}
                            key={position}
                            src={`/api/projects/${project.id}/frame/${position-1}`}
                            alt={`Frame ${position}`}
                            className={`image`} // Add selected class
                            style={selectedImageStyle(position)}
                            // onClick={() => handleImageClick(position)}
                            onMouseDown={(event) => handleMouseDown(event, position)}
                            
                            onMouseOver={() => handleMouseOver(position)}
                            onDragStart={() => preventDragHandler}
                            draggable="false"
                            />
                    </div>
                ))}
            </div>
            <div className="pagination-buttons">
                <button onClick={prevPage} disabled={pageNum === 0}>Back</button>
                <button onClick={nextPage} disabled={(pageNum + 1) * imagesPerPage >= frameCount}>Next</button>
            </div>
        </div>
    );
}